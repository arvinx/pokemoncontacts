package com.pokemoncontacts;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public enum POKEMON_GENERATION {
	GENERATION_1 (0, 151),
	GENERATION_2 (152, 251),
	GENERATION_3 (252, 386),
	GENERATION_4 (387, 493),
	GENERATION_5 (494, 647);
	
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
		return (int) (generation.getStart() + Math.ceil(Math.random()*(generation.getEnd() - generation.getStart())));
	}
	
	public static Integer getFileAppendix(POKEMON_GENERATION [] generations) {
		List<Integer> randomIndices = new ArrayList<Integer>();
		for (POKEMON_GENERATION generation : generations) {
			if (generation != null) {
				Log.d("Arvin Log", generation.name());
				randomIndices.add(getIndexInGeneration(generation));
			}
		}
		for (Integer i : randomIndices) {
			Log.d("Arvin Log", i.toString());
		}
		int randomIndex = (int) Math.floor(Math.random()*randomIndices.size());
		
		return randomIndices.get(randomIndex);
	}

}
