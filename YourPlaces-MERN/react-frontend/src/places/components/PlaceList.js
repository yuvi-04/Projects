import React from 'react'
import './PlaceList.css'
import { Card } from '../../shared/components/UIElements/Card'
import { PlaceItem } from './PlaceItem'
import { Button } from '../../shared/components/FormElements/Button'

export const PlaceList = (props) => {

    if(props.item.length === 0) {
        return <div className='place-list center'>
            <Card>
                <h2>No places Found</h2>
                <Button to='/places/new'>Share Place</Button>
            </Card>
        </div>
    }
  return (
    <ul className='place-list'>
        {
            props.item.map((place) => {
                return (
                    <PlaceItem
                        key={place.id}
                        id={place.id}
                        image={place.image}
                        title={place.title}
                        description={place.description}
                        address={place.address}
                        creatorId={place.creator}
                        coordinates={place.location}
                        onDelete={props.onDeletePLace}
                    />
                )
            })
        }
    </ul>
  )
}
