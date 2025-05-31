"use client";

import PokemonCard from "@/model/pokemonCard";
import { Card, CardSubtitle } from "react-bootstrap";
import PokemonTypeBadgeComp from "./pokemonTypeBadgeComp";

interface PokemonCardCompProps {
  pokemon: PokemonCard;
}

// Helper function to map type to border color
function getBorderColor(type: string) {
  switch (type) {
    case "Water":
      return "#0d6efd"; // Bootstrap primary
    case "Fire":
      return "#dc3545"; // Bootstrap danger
    case "Grass":
      return "#198754"; // Bootstrap success
    case "Electric":
      return "#ffc107"; // Bootstrap warning
    default:
      return "#6c757d"; // Bootstrap secondary
  }
}

export default function PokemonCardComp(props: PokemonCardCompProps) {
  const pokemonUrl = `/pokemon/${props.pokemon.pokemonNumber}`;
  const primaryType = props.pokemon.pokemonType[0];
  const borderColor = getBorderColor(primaryType);

  return (
    <a href={pokemonUrl}>
      <Card style={{width: '14rem', border: `0.2rem solid ${borderColor}`}}>
        <Card.Img 
        variant="top" 
        src={props.pokemon.mainImage}
        alt={`Image of ${props.pokemon.pokemonName}`} 
        style={{
          height: '12rem',
          objectFit: 'contain',
          background: "#323232",
          borderTopRightRadius: '0.2rem',
          borderTopLeftRadius: '0.2rem'
        }}
        />
        <Card.Body>
          <Card.Title className="fs-4">{props.pokemon.pokemonName}</Card.Title>
          <CardSubtitle className="mb-1 text-muted fs-6">
            <PokemonTypeBadgeComp pokemonTypes={props.pokemon.pokemonType} />
          </CardSubtitle>
        </Card.Body>
      </Card>
    </a>
  );
}
