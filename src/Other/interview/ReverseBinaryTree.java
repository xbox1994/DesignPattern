package Other.interview;

import java.util.LinkedList;

class TreeNode {
    public TreeNode left;
    public TreeNode right;
    public int value;

    public TreeNode(int value) {
        this.value = value;
    }

    public TreeNode invertNode(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode temp = root.left;
        root.left = invertNode(root.right);
        root.right = invertNode(temp);
        return root;
    }

    public void printTreeNode() {
        LinkedList<TreeNode> queue = new LinkedList<>();
        queue.add(this);

        TreeNode currentLineRightestNode = this;
        TreeNode nextLineRightestNode = null;

        while (!queue.isEmpty()) {
            TreeNode currentNode = queue.poll();

            if (currentNode.left != null) {
                queue.add(currentNode.left);
                nextLineRightestNode = currentNode.left;
            }
            if (currentNode.right != null) {
                queue.add(currentNode.right);
                nextLineRightestNode = currentNode.right;
            }

            System.out.print(currentNode.value);

            if (currentNode.value == currentLineRightestNode.value) {
                System.out.println();
                currentLineRightestNode.value = nextLineRightestNode.value;
            }
        }
    }
}

public class ReverseBinaryTree {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(9);
        root.printTreeNode();
        System.out.println("-------");
        root.invertNode(root);
        root.printTreeNode();
    }
}
