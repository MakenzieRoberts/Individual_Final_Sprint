const { BinarySearchTree } = require("../services/binarysearch.js");

test("should return the expected binary search tree object", async () => {
	let bst = new BinarySearchTree();
	bst.insertNode(1);
	bst.insertNode(2);
	bst.insertNode(3);
	bst.insertNode(4);
	bst.insertNode(5);
	const treeData = bst.root;
	expect(treeData).toEqual(
		expect.objectContaining({
			data: 1,
			left: null,
			right: {
				data: 2,
				left: null,
				right: {
					data: 3,
					left: null,
					right: {
						data: 4,
						left: null,
						right: { data: 5, left: null, right: null },
					},
				},
			},
		})
	);
});
