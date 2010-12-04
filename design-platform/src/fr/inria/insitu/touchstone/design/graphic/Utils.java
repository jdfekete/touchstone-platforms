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
package fr.inria.insitu.touchstone.design.graphic;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fr.inria.insitu.touchstone.design.motor.Block;


public class Utils {

	/**
	 * Used to display a block in a JTree.
	 * Create a node for each child of the given block and add them to the specified node. 
	 * @param node
	 * @param block
	 */
	static void addChild(DefaultMutableTreeNode node, Block block){
		for (Block b : block){
			DefaultMutableTreeNode n = new DefaultMutableTreeNode(b.toString());
			addChild(n,b);
			node.add(n);
		}
	}
	
	/**
	 *	If expand is true, expands all nodes in the tree.
	 * Otherwise, collapses all nodes in the tree.
	 * @param tree
	 * @param expand
	 */
	static void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode)tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}
	
	static int getDepth(JTree tree) {
		TreeNode root = (TreeNode)tree.getModel().getRoot();
		if(root.getChildCount() == 0) return 0;
		else return 1 + getDepth(root.getChildAt(0));
	}
	
	static int getDepth(TreeNode tree) {
		if(tree.getChildCount() == 0) return 0;
		else return 1 + getDepth(tree.getChildAt(0));
	}
	
	/**
	 *	If expand is true, expands all nodes in the tree.
	 * Otherwise, collapses all nodes in the tree.
	 * @param tree
	 * @param expand
	 */
	static void expandFirstNode(JTree tree) {
		expandFirstNodeAtLevel(getDepth(tree), tree);
	}
	
	static void expandFirstNodeAtLevel(int level, JTree tree) {
		if(level == 0) return;
		TreeNode root = (TreeNode)tree.getModel().getRoot();
		// Traverse tree from root
		TreePath rootPath = new TreePath(root);
		TreeNode firstChild = root.getChildAt(0);
		TreePath path = rootPath.pathByAddingChild(firstChild);
		tree.expandPath(path);
		expandNodesAtLevel(level-1, tree, path);
	}
	
	static void expandNodesAtLevel(int level, JTree tree, TreePath parent) {
		if(level == 0) return;
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
				TreeNode n = (TreeNode)e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				tree.expandPath(path);
				expandNodesAtLevel(level-1, tree, path);
			}
		}
	}
	
//	public static TreePath getTreePath(TreePath path, JTree tree) {
//		Object[] pathElements = path.getPath();
//		TreePath result = null;
//		TreeNode root = (TreeNode)tree.getModel().getRoot();
//		if((root.toString() == null && pathElements[0].toString() == null) ||
//				(root.toString().compareTo(pathElements[0].toString()) == 0))
//			result = new TreePath(root);
//		
//		if(result == null || pathElements.length == 1) return result;
//		
//		Enumeration<?> enumeration = root.children();
//		for(int i = 1; i < pathElements.length; i++) {
//			boolean found = false;
//			while(enumeration.hasMoreElements()) {
//				TreeNode next = (TreeNode)enumeration.nextElement();
//				if(next.toString().compareTo(pathElements[i].toString()) == 0) {
//					result = result.pathByAddingChild(next);
//					enumeration = next.children();
//					found = true;
//					break;
//					
//				}
//			}
//			if(!found) return null;
//		}
//		return result;
//	}
	
	public static boolean searchUnexpandedPath(JTree tree, TreePath path, int index, TreeNode node, TreePath[] result, boolean compareOnlyLabels) {
		Object[] pathElements = path.getPath();
		if(index >= pathElements.length) {
			return !tree.isExpanded(result[0]);
		}
		Object elementToSearch = pathElements[index];
		Enumeration<?> enumeration = node.children();
		while(enumeration.hasMoreElements()) {
			TreeNode next = (TreeNode)enumeration.nextElement();
			if(
					(compareOnlyLabels && next.toString().compareTo(elementToSearch.toString()) == 0)
					||
					(!compareOnlyLabels && next.equals(elementToSearch)) ){
				TreePath[] potentialResult = new TreePath[1];
				potentialResult[0] = result[0].pathByAddingChild(next);
				if(searchUnexpandedPath(tree, path, index+1, next, potentialResult, compareOnlyLabels)) {
					result[0] = potentialResult[0];
					return true;
				}
			}
		}
		return false;
	}
	
	// TODO too naive: it reopens the first node having the same name 
	// which is not yet opened
	public static TreePath getTreePath(TreePath path, JTree tree, boolean compareOnlyLabels) {
		Object[] pathElements = path.getPath();
		TreePath result = null;
		TreeNode root = (TreeNode)tree.getModel().getRoot();
		if((root.toString() == null && pathElements[0].toString() == null) ||
				(root.toString().compareTo(pathElements[0].toString()) == 0))
			result = new TreePath(root);
		
		if(result == null || pathElements.length == 1) return result;
		
		TreePath[] potentialResult = new TreePath[1];
		potentialResult[0] = result;
		boolean found = searchUnexpandedPath(tree, path, 1, root, potentialResult, compareOnlyLabels);
		if(found) return potentialResult[0];
		else return null;
	}
	

	@SuppressWarnings("unchecked")
	static void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements(); ) {
				TreeNode n = (TreeNode)e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}
	
	private static ImageIcon ICON_PARTICIPANT = null;
	private static ImageIcon ICON_EXPERIMENT = null;
	private static ImageIcon ICON_BLOCK = null;
	private static ImageIcon ICON_INTERBLOCK = null;
	private static ImageIcon ICON_TRIAL = null;
	private static ImageIcon ICON_INTERTRIAL = null;
	private static ImageIcon ICON_INTERPRACTICE = null;
	private static ImageIcon ICON_INTERPRACTICEEXP = null;
	private static ImageIcon ICON_PRACTICE = null;
	private static ImageIcon ICON_PRACTICEEXP = null;
	private static ImageIcon ICON_SETUP = null;
	
	public static ImageIcon getIconSetup() {
		if(ICON_SETUP == null) {
			File iconSetupFile = new File("icons"+File.separator+"setup.png");
			try {
				ICON_SETUP = new ImageIcon(iconSetupFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_SETUP;
	}
	
	public static ImageIcon getIconInterblockPractice() {
		if(ICON_INTERPRACTICE == null) {
			File iconInterblockPracticeFile = new File("icons"+File.separator+"interpractice.png");
			try {
				ICON_INTERPRACTICE = new ImageIcon(iconInterblockPracticeFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_INTERPRACTICE;
	}

	public static ImageIcon getIconInterblockPracticeExp() {
		if(ICON_INTERPRACTICEEXP == null) {
			File iconInterblockPracticeFile = new File("icons"+File.separator+"interpracticeexp.png");
			try {
				ICON_INTERPRACTICEEXP = new ImageIcon(iconInterblockPracticeFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_INTERPRACTICEEXP;
	}
	
	public static ImageIcon getIconPractice() {
		if(ICON_PRACTICE == null) {
			File iconPracticeFile = new File("icons"+File.separator+"practice.png");
			try {
				ICON_PRACTICE = new ImageIcon(iconPracticeFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_PRACTICE;
	}
	
	public static ImageIcon getIconPracticeExp() {
		if(ICON_PRACTICEEXP == null) {
			File iconPracticeFile = new File("icons"+File.separator+"practiceexp.png");
			try {
				ICON_PRACTICEEXP = new ImageIcon(iconPracticeFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_PRACTICEEXP;
	}
	
	public static ImageIcon getIconSubject() {
		if(ICON_PARTICIPANT == null) {
			File iconSubjectFile = new File("icons"+File.separator+"silhouette-icon.jpg");
			try {
				ICON_PARTICIPANT = new ImageIcon(iconSubjectFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_PARTICIPANT;
	}
	
	public static ImageIcon getIconExpe() {
		if(ICON_EXPERIMENT == null) {
			File iconExpeFile = new File("icons"+File.separator+"expe-icon.jpg");
			try {
				ICON_EXPERIMENT = new ImageIcon(iconExpeFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_EXPERIMENT;
	}
	
	public static ImageIcon getIconBlock() {
		if(ICON_BLOCK == null) {
			File iconBlockFile = new File("icons"+File.separator+"block.png");
			try {
				ICON_BLOCK = new ImageIcon(iconBlockFile.toURI().toURL());
//				URL url = Utils.class.getResource("icons"+File.separator+"block.png");
//				ICON_BLOCK = new ImageIcon(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_BLOCK;
	}
	
	public static ImageIcon getIconInterBlock() {
		if(ICON_INTERBLOCK == null) {
			File iconInterBlockFile = new File("icons"+File.separator+"interblock.png");
			try {
				ICON_INTERBLOCK = new ImageIcon(iconInterBlockFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_INTERBLOCK;
	}
	
	public static ImageIcon getIconTrial() {
		if(ICON_TRIAL == null) {
			File iconTrialFile = new File("icons"+File.separator+"trial.png");
			try {
				ICON_TRIAL = new ImageIcon(iconTrialFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_TRIAL;
	}
	
	public static ImageIcon getIconInterTrial() {
		if(ICON_INTERTRIAL == null) {
			File iconInterTrialFile = new File("icons"+File.separator+"intertrial.png");
			try {
				ICON_INTERTRIAL = new ImageIcon(iconInterTrialFile.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return ICON_INTERTRIAL;
	}
		
		
	
}
