package com.pokemoncontacts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum POKEMON_GENERATION implements Serializable {
	GENERATION_1 (0, 151),
	GENERATION_2 (152, 251),
	GENERATION_3 (252, 386),
	GENERATION_4 (387, 493),
	GENERATION_5 (494, 647),
	GENERATION_X (0, 163);
	
	private final int start;
	private final int end;
	
	POKEMON_GENERATION (int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return this.start;
	}
	
	public int getEnd() {
		return this.end;
	}
	
	public static int getTotalPokemon() {
		return GENERATION_5.end + GENERATION_X.end + 1;
	}
	
	public static int getIndexInGeneration(POKEMON_GENERATION generation) {
		return (int) (generation.getStart() + Math.ceil(Math.random()*(generation.getEnd() - generation.getStart())));
	}
	
	public static Integer getRandomFileAppendix() {
		List<Integer> randomIndices = new ArrayList<Integer>();
		for (POKEMON_GENERATION generation : PokemonCollection.generationsSelected) {
			if (generation != null) {
				Integer randNum = getIndexInGeneration(generation);
				randomIndices.add(randNum);
			}
		}
		int randomIndex = (int) Math.floor(Math.random()*randomIndices.size());
		
		return randomIndices.get(randomIndex);
	}

}
