const uuid = require('uuid')
const fs = require('fs')
const { validationResult } = require('express-validator')
const mongoose = require('mongoose')

const HttpError = require('../models/http-error')
const getCoordsForAddress = require('../util/location')
const Place = require('../models/place')
const User = require('../models/user')

const getPlaceById = async (req, res, next) => {
    const placeId = req.params.pid

    await Place.findById(placeId).exec()
    .then((place => {
        res.json({ place: place.toObject({ getters: true }) })
    }))
    .catch(err => {
        return next(new HttpError('Could not find a place', 404))
    })
}

const getPlaceByUserId = async (req, res, next) => {
    const userId = req.params.uid

    await Place.find({ creator: userId }).exec() //add if() for when result is empty array
    .then((place) => {
        res.json({ place: place.map(plc => plc.toObject({ getters: true })) })
    })
    .catch(err => {
        console.log(err);
        return next(new HttpError('Could not find a place', 404))
    })
}

const createPlace = async (req, res, next) => {
    const errors = validationResult(req)
    if(!errors.isEmpty()) {
        return next(new HttpError('Invalid Input!!', 422))
    }

    const { title, description, address } = req.body

    let coordinates
    try {
        coordinates = await getCoordsForAddress(address)
    } catch (error) {
        return next()
    }
    
    const createdPlace = new Place({
        title,
        description,
        address,
        location: coordinates,
        image: req.file.path,
        creator: req.userData.userId
    })

    let user
    await User.findById(req.userData.userId)
    .exec()
    .then(value => {
        if(value) {
            user = value
        } else {
            console.log(value);
            return next(new HttpError('Creator not found', 404))
        }
    })
    .catch(err => {
        console.log(err);
        return next(new HttpError('Something went Wrong!', 500))
    })
    if(!user) return

    await mongoose.startSession()
    .then((sess) => {
        sess.startTransaction()
        createdPlace.save({ session: sess})
        user.places.push(createdPlace)
        user.save({ session: sess })
        sess.commitTransaction()
        .catch(err => console.log(err))
        res.status(201).json({ createdPlace })
    })
    .catch(err => {
        console.log(err);
        return next(new HttpError('Session Problem', 500))
    })
}

const updatePlace = async (req, res, next) => {
    const errors = validationResult(req)
    if(!errors.isEmpty()) {
        throw new HttpError('Invalid Input!!', 422)
    }
    
    const { title, description } = req.body
    const placeId = req.params.pid

    await Place.findById(placeId).exec()
    .then((place => {
        if(place.creator.toString() !== req.userData.userId) {
            throw new Error('Not authorized to update this place!!')
        }
        place.title = title
        place.description = description

        place.save()
        .then((result) => {
            res.status(200).json({ place: place.toObject({ getters: true }) })
        })
        .catch(err => {
            return next(new HttpError('Cannot Update Place', 304))
        })
    }))
    .catch(err => {
        return next(new HttpError('Could not find place to Update', 404))
    })
}

const deletePlace = async (req, res, next) => {
    const placeId = req.params.pid

    await mongoose.startSession()
    .then((session) => {
        session.startTransaction()
        Place.findByIdAndDelete(placeId, { session: session }).populate('creator').exec()
        .then((placeRemoved) => {
            // if(placeRemoved.creator._id.toString() !== req.userData.id) {
            //     throw new Error('Not authorized to Delete this place!!')
            // }
            placeRemoved.creator.places.pull(placeRemoved)
            placeRemoved.creator.save({ session: session })
            .then(() => {
                session.commitTransaction()
                fs.unlink(placeRemoved.image, (err) => console.log(err))
                res.json('Place Deleted Successfully')
            })
            .catch(err => {
                console.log('User Not Update Error: ' + err);
                return next(new HttpError('Cannot Update place in user', 500))
            })
        })
        .catch((err) => {
            console.log("My Error: " + err);
            return next(new HttpError('Cannot Delete Place', 500))
        })
    })
    .catch(err => {
        console.log("Session Error" + err);
        return next(new HttpError('Server Error', 500))
    })
}

exports.getPlaceById = getPlaceById
exports.getPlaceByUserId = getPlaceByUserId
exports.createPlace = createPlace
exports.updatePlace = updatePlace
exports.deletePlace = deletePlace