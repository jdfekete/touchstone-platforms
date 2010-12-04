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
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public abstract class OrderingMode implements Serializable {

	private static final long serialVersionUID = 42L;
	
	/**
	 * Reorders the children of the blocks passed in argument.
	 * @param blocks the block whose children will be reordered
	 * @throws Exception if any error.
	 */
	public void order(Vector<Block> blocks, boolean serial) throws Exception {
		for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext();) {
			Block next = iterator.next();
			next.setOrderingMode(this);
			next.setSerial(serial);
		}
	}

	/**
	 * Reorders the elements of a vector depending on the given order.
	 * @param v the vector of elements to reorder
	 * @param order a vector of Integer. The first element 
	 * of v will be moved to the index contained in the first element of the vector order.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Vector reorderElements(Vector v, Vector<Integer> order){
		if (order.size()>=v.size()){
			Vector result = (Vector) v.clone();
			for (int i =0; i< order.size() ; i++){
				result.set(order.get(i), v.get(i));
			}
			return result;
		}
		else {
			return null;
		}
	}

	/**
	 * Created a shuffled copy of a given vector.
	 * @param v the vector to shuffle
	 * @return the shuffled vector
	 */
	@SuppressWarnings("unchecked")
	public Vector shuffle(Vector v){
		Vector copy = (Vector) v.clone();
		Vector result = (Vector) v.clone();
		result.removeAllElements();
		Random generator = new Random();
		while(copy.size()>0){
			int index = generator.nextInt(copy.size());
			result.add(copy.get(index));
			copy.remove(index);
		}
		return result;
	}
	
	/**
	 * the factorial function
	 * @param n
	 * @return n!
	 */
	public long factorial(int n){
		long result = 1;
		if (n<=1)
			return result;
		else
			for (int i = 2 ;i<=n;i++)
				result*=i;
		return result;
	}
}
