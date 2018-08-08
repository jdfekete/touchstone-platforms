/*   TouchStone design platform is a software to design protocols for lab        *
 *   experiments. It is published under the terms of a BSD license               *
 *   (see details below)                                                         *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone design platform reuses parts of an early version which were      *
 *   programmed by Matthis Gilbert.                                              *
 *********************************************************************************/
/* Redistribution and use in source and binary forms, with or without            * 
 * modification, are permitted provided that the following conditions are met:   *

 *  - Redistributions of source code must retain the above copyright notice,     *
 *    this list of conditions and the following disclaimer.                      *
 *  - Redistributions in binary form must reproduce the above copyright notice,  *
 *    this list of conditions and the following disclaimer in the documentation  *
 *    and/or other materials provided with the distribution.                     *
 *  - Neither the name of the INRIA nor the names of its contributors   *
 * may be used to endorse or promote products derived from this software without *
 * specific prior written permission.                                            *

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"   *
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE     *
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE    *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE     *
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR           *
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF          *
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS      *
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN       *
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)       *
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE    *
 * POSSIBILITY OF SUCH DAMAGE.                                                   *
 *********************************************************************************/
package fr.inria.insitu.touchstone.design.motor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Vector;

public class BalancedLatinSquare extends OrderingMode implements Serializable {

	private static final long serialVersionUID = 42L;
	
	private static Vector<Vector<Integer>> makeLatinSquare(int n){
		Vector<Vector<Integer>> latinSquare = new Vector<Vector<Integer>>();
		Vector<Integer> firstRow = new Vector<Integer>();

		firstRow.add(0);
		firstRow.add(1);
		for (int i = 2; i < n; i += 1) {
			firstRow.add((i % 2) == 0 ? n - i / 2 :  i / 2 + 1 );
		}
		latinSquare.add(firstRow);

		for (int i = 1; i < n; i += 1) {
			Vector<Integer> row = new Vector<Integer>();
			for (int j = 0; j < n; j += 1) {
				row.add((latinSquare.get(i - 1).get(j) + 1) % (n));
			}
			latinSquare.add(row);
		}

		// In case there is an odd number of treatments, we also need to add the reversed
		// rows.
		if ((n % 2) > 0) {
			for (int i = 0; i < n; i += 1) {
				Vector<Integer> row = new Vector<Integer>(latinSquare.get(i));
				Collections.reverse(row);
				latinSquare.add(row);
			}
		}
		return latinSquare;
	}
	
	@SuppressWarnings("unchecked")
	public void order(Vector<Block> blocks, boolean serial) throws Exception {
		super.order(blocks, serial);
		int side = blocks.get(0).size();
	
		
		// The latinSquare matrix give the index of the element that should be at the position i
		// instead of the position of the element i. Transform this.
		Vector<Vector<Integer>> positionsLatinSquare = new Vector<Vector<Integer>>();
		for(Vector<Integer> row: makeLatinSquare(side)) {
			Vector<Integer> positionRow = new Vector<Integer>(row);
			int positionI = 0;
			for(int elementI: row) {
				positionRow.set(elementI, positionI);
				positionI += 1;
			}
			positionsLatinSquare.add(positionRow);
		}

		positionsLatinSquare = shuffle(positionsLatinSquare);

		int i = 0;
		for (Block block : blocks) {
			Vector<Block> newOrder = reorderElements(block, positionsLatinSquare.get(i));
			block.removeAllElements();
			block.addAll(newOrder);
			i = (i + 1) % positionsLatinSquare.size();
		}
		
		if (blocks.size() < positionsLatinSquare.size())
			throw new Exception(
				positionsLatinSquare.size() +
				" replications were expected ( " +
				(positionsLatinSquare.size() - blocks.size()) +
				" missing )"
			);
	}

	
	public String toString() {
		return "Balanced Latin Square";
	}
}
