const express = require('express')
const bodyparser = require('body-parser')
const mongoose = require('mongoose')

const fs = require('fs')
const path = require('path')

const placesRoutes = require('./routes/place-routes')
const usersRoutes = require('./routes/user-routes')
const HttpError = require('./models/http-error')

const app = express()

app.use(bodyparser.json())

app.use('/uploads/images', express.static(path.join('uploads', 'images')))

app.use((req, res, next) => {
    res.setHeader('Access-Control-Allow-Origin', '*')
    res.setHeader(
        'Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization'
    )
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PATCH, DELETE')
    next()
})

app.use('/places', placesRoutes)
app.use('/users', usersRoutes)

app.use((req, res, next) => {
    const error = new HttpError('Route not found', 404)
    next(error)
})

app.use((error, req, res, next) => {
    if(req.file) {
        fs.unlink(req.file.path, (err) => {
            console.log(err);
        })
    }
    if(res.headerSent) {
        return next(error)
    }
    res.status(error.code || 500).json({ message: error.message || 'Unknown Error Occurred!!' })
})

mongoose.connect(`mongodb+srv://${process.env.DB_USER}:${process.env.DB_PASSWORD}@cluster0.zsmmsml.mongodb.net/${process.env.DB_NAME}?retryWrites=false&w=majority&appName=Cluster0`)
.then(() => {
    console.log('database connected');
    app.listen(5000)
})
.catch((error) => {
    console.log(error);
})