import React, {  useContext, useState } from 'react'
import './Auth.css'
import { Input } from '../../shared/components/FormElements/Input'
import { VALIDATOR_EMAIL, VALIDATOR_MINLENGTH, VALIDATOR_REQUIRE } from '../../shared/util/validators'
import { useForm } from '../../shared/hooks/form-hook'
import { Card } from '../../shared/components/UIElements/Card'
import { Button } from '../../shared/components/FormElements/Button'
import { ErrorModal } from '../../shared/components/UIElements/ErrorModal'
import { LoadingSpinner } from '../../shared/components/UIElements/LoadingSpinner'
import { AuthContext } from '../../shared/context/auth-context'
import { useHttpClient } from '../../shared/hooks/http-hook'
import { ImageUpload } from '../../shared/components/FormElements/ImageUpload'

export const Auth = () => {
    const auth = useContext(AuthContext)
    const [isLoginMode, setIsLoginMode] = useState(true)
    const { isLoading, error, sendRequest, clearError} = useHttpClient()

    const [formState, inputHandler, setFormData] = useForm({
        email: {
            value: '',
            isValid: false
        },
        password: {
            value: '',
            isValid: false
        }
    }, false)

    const switchModeHandler = () => {
        if(!isLoginMode) {
            setFormData({
                ...formState.inputs,
                name: undefined,
                image: undefined
            }, formState.inputs.email.isValid && formState.inputs.password.isValid)
        } else {
            setFormData({
                ...formState.inputs,
                name: {
                    value: '',
                    isValid: false
                },
                image: {
                    value: null,
                    isValid: false
                }
            }, false)
        }
        setIsLoginMode(prevMode => !prevMode)
    }

    const authSubmitHandler = async (event) => {
        event.preventDefault()

        if(isLoginMode) {
            try {
                const responseData = await sendRequest(
                    `${process.env.REACT_APP_BACKEND_URL}/users/login`,
                    'POST', 
                    JSON.stringify({
                        email: formState.inputs.email.value,
                        password: formState.inputs.password.value
                    }),
                    {
                        'Content-Type': 'application/json'
                    }
                )
                console.log(responseData.message);
                auth.login(responseData.userId, responseData.token)
            } catch(err) {
                console.log(err);
            }
        }
        else {
            try {
                const formData = new FormData()
                formData.append('email', formState.inputs.email.value)
                formData.append('name', formState.inputs.name.value)
                formData.append('password', formState.inputs.password.value)
                formData.append('image', formState.inputs.image.value)
                const responseData = await sendRequest(
                    `${process.env.REACT_APP_BACKEND_URL}/users/signup`,
                    'POST',
                    formData
                )
                auth.login(responseData.userId, responseData.token)
            } catch(err) {
                console.log(err);
            }
        }
    }

  return (
    <>
    <ErrorModal error={error} onClear={clearError} />
    <Card className='authentication' >
        {isLoading && <LoadingSpinner asOverlay />}
        <h2 className='h2'>LOGIN PAGE</h2>
        <hr />
        <form onSubmit={authSubmitHandler} >
            {!isLoginMode && 
                <Input
                    id='name'
                    element='input'
                    type='text'
                    label='NAME'
                    validators={[VALIDATOR_REQUIRE()]}
                    errorText='Enter a name'
                    onInput={inputHandler}
                />
            }
            {!isLoginMode &&
                <ImageUpload
                    center
                    id="image"
                    onInput={inputHandler}
                />
            }
            <Input
                id='email'
                element='input'
                type='email'
                label='EMAIL'
                validators={[VALIDATOR_EMAIL(), VALIDATOR_REQUIRE()]}
                errorText='Enter a Valid Email'
                onInput={inputHandler}
            />
            <Input
                id='password'
                element='input'
                type='password'
                label='PASSWORD'
                validators={[VALIDATOR_REQUIRE(), VALIDATOR_MINLENGTH(7)]}
                errorText='Password too short! Minimum 8 characters Required'
                onInput={inputHandler}
            />
            <Button type='submit' disabled={!formState.isValid} >{isLoginMode ? 'LOGIN' : 'SIGN UP'}</Button>
        </form>
        <Button inverse onClick={switchModeHandler} >{isLoginMode ? 'SIGN UP' : 'LOGIN'}</Button>
    </Card>
    </>
  )
}
