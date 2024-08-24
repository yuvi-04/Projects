const { validationResult } = require('express-validator')
const bcrypt = require('bcryptjs')
const jwt = require('jsonwebtoken')

const HttpError = require('../models/http-error')
const User = require('../models/user')

const getUsers = async (req, res, next) => {
    User.find({}, '-password').exec()
    .then((result) => {
        if(result.length === 0) {
            return next(new HttpError('No Users Found!', 404))
        }
        else {
            res.json({ users: result.map(val => val.toObject({ getters: true }))})
        }
    })
    .catch(err => {
        console.log(err);
        return next(new HttpError('Something went Wrong', 500))
    })
}

const signup = async (req, res, next) => {
    const errors = validationResult(req)
    if(!errors.isEmpty()) {
        throw new HttpError('Check the entered Input!!', 422)
    }

    const { name, email, password } = req.body

    let flag = 0
    await User.find({ email: email }).exec()
    .then((result) => {
        if(result.length !== 0) {
            flag = 1
            return next(new HttpError('User Already Exists!!', 422))
        }
    })
    .catch(err => {
        console.log(err);
        return next(new HttpError('Something went Wrong!', 500))
    })
    if(flag) return

    let hashPassword
    try {
        hashPassword = await bcrypt.hash(password, 12)
    } catch(err) {
        return next(new HttpError('Could not hash password', 500))
    }

    const createdUser = new User({
        name,
        email,
        password: hashPassword,
        image: req.file.path,
        places: []
    })

    await createdUser.save()
    .then((result) => {
        const token = jwt.sign({ userId: result.id, email: result.email }, process.env.JWT_KEY, { expiresIn: '1h' })
        res.json({ userId: result.id, email: result.email, token: token})
    })
    .catch(err => {
        console.log(err);
        return next(new HttpError('Cannot create User',500))
    })
}

const login = async (req, res, next) => {
    const { email, password } = req.body

    User.find({ email: email }).exec()
    .then((result) => {
        if(result.length === 0) {
            return next(new HttpError('User not Foud', 404))
        }
        bcrypt.compare(password, result[0].toObject().password)
        .then(isValidPassword => {
                if(!isValidPassword) return next(new HttpError('Incorrect Password', 401))
                else {
                    const token = jwt.sign({ userId: result[0].toObject()._id, email: result[0].toObject().email }, process.env.JWT_KEY, { expiresIn: '1h' })
                    res.json({
                    message: `Welcome ${result[0].toObject().name}`,
                    userId: result[0].toObject()._id,
                    email: result.email,
                    token: token
                })
            }
        })
    })
    .catch(err => {
        console.log(err);
        return next(new HttpError('Something went Wrong!', 500))
    })
}

exports.getUsers = getUsers
exports.login = login
exports.signup = signup