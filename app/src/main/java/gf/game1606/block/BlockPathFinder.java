package gf.game1606.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import gf.game1606.Application;

public class BlockPathFinder
{
	private static int[][] availableBlocks = new int[Application.BLOCK_NUM][Application.BLOCK_NUM]; // composed with 0, 1
	private static int[][][] previousBlocks = new int[Application.BLOCK_NUM][Application.BLOCK_NUM][2]; // i, j, previous i, previous j
	private static int[][] hBlockCosts = new int[Application.BLOCK_NUM][Application.BLOCK_NUM];
	private static int[][] gBlockCosts = new int[Application.BLOCK_NUM][Application.BLOCK_NUM];
	private static List<int[]> openList;
	private static List<int[]> closedList;

	public static void setAvailableBlocks(int[][] blockLevels, ArrayList<Block> selectedBlocks, int level)
	{
		int i, j;
		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
			{
				if (blockLevels[i][j] == level)
					availableBlocks[i][j] = 1;
				else
					availableBlocks[i][j] = 0;
			}
		}

		for (Block block : selectedBlocks)
		{
			availableBlocks[block.getI()][block.getJ()] = 0;
		}
	}

	public static List<int[]> findPath(int oldI, int oldJ, int newI, int newJ)
	{
		if (!isValidIndex(new int[]{oldI, oldJ, newI, newJ}))
			return null; // invalid index!

		int i, j;
		openList = new LinkedList<int[]>();
		closedList = new LinkedList<int[]>();
		for (i = 0; i < Application.BLOCK_NUM; i++)
		{
			for (j = 0; j < Application.BLOCK_NUM; j++)
			{
				previousBlocks[i][j] = new int[]{-1, -1}; // Initialize previousBlocks
				hBlockCosts[i][j] = Math.abs(newI - i) + Math.abs(newJ - j);
				gBlockCosts[i][j] = 0;
			}
		}

		openList.add(new int[]{oldI, oldJ}); // add starting node to open list

		int[] current;
		while (true)
		{
			current = lowestFInOpen(); // get node with lowest fCosts from openList
			closedList.add(current); // add current node to closed list
			openList.remove(current); // delete current node from open list

			if ((current[0] == newI) && (current[1] == newJ))
				return calculatePath(new int[]{oldI, oldJ}, new int[]{newI, newJ}); // found goal

			// If current is not the goal node, do this for all adjacent nodes:
			List<int[]> adjacentNodes = getAdjacentNodes(current[0], current[1]);
			for (i = 0; i < adjacentNodes.size(); i++)
			{
				int[] currentAdjacentNodes = adjacentNodes.get(i);
				if (!listContains(openList, currentAdjacentNodes[0], currentAdjacentNodes[1]))
				{
					// New node! This node is not in openList
					previousBlocks[currentAdjacentNodes[0]][currentAdjacentNodes[1]] = current;
					setgCosts(currentAdjacentNodes);
					openList.add(currentAdjacentNodes); // add nodes to openList
				}
				else
				{
					// This adjacent node is already in openList
					if (getgCosts(currentAdjacentNodes) > getgCosts(current) + 1)
					{
						// costs from current node are cheaper than previous costs
						previousBlocks[currentAdjacentNodes[0]][currentAdjacentNodes[1]] = current;
						setgCosts(currentAdjacentNodes);
					}
				}
			}

			if (openList.isEmpty())
				return null; // unreachable // no path exists
		}
	}

	private static List<int[]> calculatePath(int[] start, int[] goal)
	{
		// Return path goal to start by searching previousBlocks.
		// If previousBlocks is not perfect, this method will result null or in an infinite loop!
		LinkedList<int[]> path = new LinkedList<int[]>();
		int[] current = goal;
		boolean done = false;

		while (!done)
		{
			path.addFirst(current);
			current = previousBlocks[current[0]][current[1]];

			if (Arrays.equals(current, start))
				done = true;
			else if (Arrays.equals(current, new int[]{-1, -1}))
				return null;
		}

		return path;
	}

	private static int[] lowestFInOpen()
	{
		int[] cheapestNode = openList.get(0);
		for (int[] node : openList)
		{
			if (gBlockCosts[node[0]][node[1]] + hBlockCosts[node[0]][node[1]] < gBlockCosts[cheapestNode[0]][cheapestNode[1]] + hBlockCosts[cheapestNode[0]][cheapestNode[1]])
			{
				cheapestNode = node;
			}
		}
		return cheapestNode;
	}

	private static List<int[]> getAdjacentNodes(int i, int j)
	{
		List<int[]> adj = new ArrayList<int[]>();

		if (i > 0)
			if (availableBlocks[i - 1][j] == 1 && !listContains(closedList, i - 1, j))
				adj.add(new int[]{i - 1, j});
		if (i < Application.BLOCK_NUM - 1)
			if (availableBlocks[i + 1][j] == 1 && !listContains(closedList, i + 1, j))
				adj.add(new int[]{i + 1, j});
		if (j > 0)
			if (availableBlocks[i][j - 1] == 1 && !listContains(closedList, i, j - 1))
				adj.add(new int[]{i, j - 1});
		if (j < Application.BLOCK_NUM - 1)
			if (availableBlocks[i][j + 1] == 1 && !listContains(closedList, i, j + 1))
				adj.add(new int[]{i, j + 1});

		return adj;
	}

	private static void setgCosts(int[] node)
	{
		int gCost = 0;
		int i = previousBlocks[node[0]][node[1]][0];
		int j = previousBlocks[node[0]][node[1]][1];

		while (i >= 0 && j >= 0)
		{
			gCost++;
			int[] temp = previousBlocks[i][j];
			i = temp[0];
			j = temp[1];
		}

		gBlockCosts[node[0]][node[1]] = gCost;
	}

	private static int getgCosts(int[] node)
	{
		return gBlockCosts[node[0]][node[1]];
	}

	private static boolean isValidIndex(int index)
	{
		return (index >= 0 && index < Application.BLOCK_NUM);
	}

	private static boolean isValidIndex(int[] index)
	{
		for (int i : index)
		{
			if (!isValidIndex(i))
				return false;
		}
		return true;
	}

	private static boolean listContains(List<int[]> list, int i, int j)
	{
		for (int[] index : list)
		{
			if (index[0] == i && index[1] == j)
				return true;
		}
		return false;
	}
}
