package com.pokemoncontacts;

import java.util.ArrayList;
import java.util.List;

public enum POKEMON_GENERATION {
	GENERATION_1 (0, 151),
	GENERATION_2 (0, 151),
	GENERATION_3 (0, 151),
	GENERATION_4 (0, 151),
	GENERATION_5 (0, 151);
	
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
	
	public static int getIndexInGeneration(POKEMON_GENERATION generation) {
		return (int) (generation.getStart() + Math.ceil(Math.random()*generation.getEnd()));
	}
	
	public static int getFileAppendix(POKEMON_GENERATION [] generations) {
		List<Integer> randomIndices = new ArrayList<Integer>();
		for (POKEMON_GENERATION generation : generations) {
			randomIndices.add(getIndexInGeneration(generation));
		}

		int randomIndex = (int) Math.floor(Math.random()*randomIndices.size());
		
		return (int)randomIndices.get(randomIndex);
	}

}
