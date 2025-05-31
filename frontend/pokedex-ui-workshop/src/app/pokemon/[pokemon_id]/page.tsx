// Instructs React (Next.js) to run this code on the client side.
// Next.js by default would render this content on the server side where the application is hosted.
"use client";

import Pokemon from '@/model/pokemon';
import React, { useEffect, useState } from 'react';
import { Row, Col, Container } from 'react-bootstrap';
import PokeNavBar from '@/components/pokeNavBarComp';

// This type is used to get the pokemon id from the url path
type Params = {
  params: { pokemon_id: string }
}

/*
useEffect: lets your component connect to and synchronize with external systems. In our case we will use it to fetch the pokemons.json file and later to connect to the backend API.
useState: lets a component “remember” information. In our case we will use it to store the information about the Pokémon.
*/

export default function PokemonPage({ params }: Params) {
    // pokemon - A constant state variable which stores the pokemon information and retains the data between renders.
    // setPokemon - A state setter function to update the variable and trigger React to render the component again.
    const pokemon_id = params.pokemon_id;
    const [pokemon, setPokemon] = useState<Pokemon>();

    useEffect(() => {
        const fetchPokemon = async () => {
            const response = await fetch('/pokemons.json');
            // Creating a Map out of the raw json
            const pokemonsMap: Map<string, Pokemon> = new Map(Object.entries(await response.json()));
            // Getting the pokemon by id
            const currentPokemon = pokemonsMap.get(pokemon_id);
            setPokemon(currentPokemon);
            console.log(currentPokemon);
        };

        fetchPokemon() // Making sure to log errors on the console
           .catch(error => {
               console.error(error);
           });
    }, []);
    
    return (
         <Container>
           <Row className="justify-content-md-center">
               <Col md="auto"><h1>{pokemon?.pokemonName}</h1></Col>
           </Row>
           <Row>
               <Col >
                   Pokemon Image
               </Col>
               <Col>
                   Pokemon Properties
               </Col>
           </Row>
       </Container>
    );
}
