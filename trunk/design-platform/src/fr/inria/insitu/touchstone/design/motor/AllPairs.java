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

import java.util.Vector;

public class AllPairs extends OrderingMode {

	private static final long serialVersionUID = 42L;
	
	private int blockNumber;
	private Vector<Block> blocks;

	
	public void order(Vector<Block> blocks, boolean serial) throws Exception {
		super.order(blocks, serial);
		this.blocks = blocks;
		this.blockNumber = -1;
		int size = blocks.get(0).size();
		int[] v = new int[size];
		for (int i=0; i<size; i++) 
			v[i] = i;
		try {
			perm (v, size, 0);
		} catch (Exception e){}
				
		long expectedPermutations = factorial(size);
//		if (blocks.size()<expectedPermutations)
		if (blocks.size()!=expectedPermutations)
			throw new Exception(expectedPermutations+" replications were expected ( "+(expectedPermutations-blocks.size())+" missing )");
		
	}

	private Block nextBlock(){
		blockNumber++;
		return blocks.get(blockNumber);
	}
	private boolean hasMoreElements(){
		return blocks.size()>blockNumber;
	}

	/* function to swap array elements */
	private  void swap (int[] v, int i, int j) {
		int	t;
		t = v[i];
		v[i] = v[j];
		v[j] = t;
	}

	/* recursive function to generate permutations */
	@SuppressWarnings("unchecked")
	private void perm (int[] v, int n, int i) throws Exception {

		/* this function generates the permutations of the array
		 * from element i to element n-1
		 */
		int	j;

		/* if we are at the end of the array, we have one permutation
		 * we can use
		 */
		if (i == n) {
			Vector<Integer> order = new Vector<Integer>();
			for (j=0; j<n; j++)
				order.add(new Integer(v[j]));
			if (hasMoreElements()) {
				Block block = nextBlock();
				Vector<Block> newOrder = reorderElements(block, order);
				block.removeAllElements();
				block.addAll(newOrder);
			}
			else
				throw new Exception();
				
			//orders.add(order);				
		} else{
			/* recursively explore the permutations starting
			 * at index i going through index n-1
			 */
			boolean stop = false;
			for (j=i; (j<n)&&(!stop); j++) {
				/* try the array with i and j switched */
				swap (v, i, j);
				try{
					perm (v, n, i+1);
				} catch (Exception e){
					stop = true;
					throw new Exception();
				}
				/* swap them back the way they were */
				swap (v, i, j);
			}
		}
	}

	
	public String toString() {
		return "All pairs";
	}
}
