package edu.stanford.ee368.flowchargenerator.imageproc;


public class GetTail {
	public static int[][] findTail(int[][][] table, int[][] center, int[] len){

		int[][] tail;
		tail = new int [len.length][2];
		
		int r = len.length;
		double maxDistance = -1;
		int tailx = 0; int taily = 0;
		for (int i = 0; i < r; i++) {
			maxDistance = -1;
			int centerx = center[i][0];
			int centery = center[i][1];
			
			for(int j = 0; j < len[i]; j++) {
				int candidatex = table[i][0][j];
				int candidatey = table[i][1][j];
				double distance = Math.sqrt((centerx - candidatex)^2 + (centery - candidatey)^2);
				if(distance > maxDistance) {
					maxDistance = distance;
					tailx = candidatex;
					taily = candidatey;
				}
			}
			
			tail[i][0] = tailx;
			tail[i][1] = taily;
		}
		
		return tail;
	}
	
}
