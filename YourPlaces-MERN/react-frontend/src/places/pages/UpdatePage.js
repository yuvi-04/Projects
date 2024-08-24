import React, { useContext, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Input } from '../../shared/components/FormElements/Input'
import { Button } from '../../shared/components/FormElements/Button'
import { VALIDATOR_MINLENGTH, VALIDATOR_REQUIRE } from '../../shared/util/validators'
import './PlaceForm.css'
import { useForm } from '../../shared/hooks/form-hook'
import { Card } from '../../shared/components/UIElements/Card'
import { useHttpClient } from '../../shared/hooks/http-hook'
import { LoadingSpinner } from '../../shared/components/UIElements/LoadingSpinner'
import { ErrorModal } from '../../shared/components/UIElements/ErrorModal'
import { AuthContext } from '../../shared/context/auth-context'

export const UpdatePage = () => {
    const auth = useContext(AuthContext)
    const {isLoading, error, sendRequest, clearError} = useHttpClient()
    const [loadedPlace, setLoadedPlace] = useState()
    const navigate = useNavigate()

    const placeId = useParams().placeId

    const [formState, inputHandler, setFormData] = useForm({
        title: {
            value: '',
            isValid: false
        },
        description: {
            value: '',
            isValid: false
        }
    }, false)

    useEffect(() => {
        const fetchPlace = async () => {
            try {
                const responseData = await sendRequest(`${process.env.REACT_APP_BACKEND_URL}/places/${placeId}`)
                setLoadedPlace(responseData.place)
                setFormData({
                    title: {
                        value: responseData.place.title,
                        isValid: true
                    },
                    description: {
                        value: responseData.place.description,
                        isValid: true
                    }
                }, true)
            } catch(err) {
                console.log(err);
            }
        }
        fetchPlace()
    }, [sendRequest, placeId, setFormData])

    const placeUpdateSubmitHandler = async () => {
        try {
            await sendRequest(
                `${process.env.REACT_APP_BACKEND_URL}/places/${placeId}`,
                'PATCH',
                JSON.stringify({
                    title: formState.inputs.title.value,
                    description: formState.inputs.description.value
                }),
                {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + auth.token
                }
            )
            navigate('/' + auth.userId + '/places')
        } catch(err) {
            console.log(err``);
        }
    }

    if(isLoading) {
        return <div className='center'>
            <LoadingSpinner />
        </div>
    }

    if(!loadedPlace && !error) {
        return <div className='center'>
            <Card><h2>Could Not Find Place!!!</h2></Card>
        </div>
    }

  return (
    <>
    <ErrorModal error={error} onClear={clearError} />
    {!isLoading && loadedPlace && <form className='place-form'
        onSubmit={placeUpdateSubmitHandler} >
        <Input
            id='title'
            element='input'
            type='text'
            label='TITLE'
            validators={[VALIDATOR_REQUIRE()]}
            onInput={inputHandler}
            value={loadedPlace.title}
            valid={true}
        />
        <Input
            id='description'
            element='textarea'
            label='Description'
            validators={[VALIDATOR_MINLENGTH(5)]}
            onInput={inputHandler}
            value={loadedPlace.description}
            valid={true}
        />
        <Button type='submit' disabled={!formState.isValid}>UPDATE</Button>
    </form>}
    </>
  )
}
