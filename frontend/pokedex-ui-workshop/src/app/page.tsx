"use client";

import PokeNavBar from "@/components/pokeNavBarComp";
import PokemonsComp from "@/components/pokemonsComp";

export default function Home(){
 const testData = [
   {
     pokemonNumber: 1,
     pokemonName:"yigit",
     pokemonType:["Fire"],
     mainImage: "https://raw.githubusercontent.com/HybridShivam/Pokemon/master/assets/images/001.png"
   },
   {
     pokemonNumber: 2,
     pokemonName:"hüso",
     pokemonType:["Water"],     
     mainImage: "https://raw.githubusercontent.com/HybridShivam/Pokemon/master/assets/images/002.png"
   },
 ];

  return (
    <>
      <PokeNavBar></PokeNavBar>
      <PokemonsComp pokemons={testData}></PokemonsComp>
    </>
  );
}

