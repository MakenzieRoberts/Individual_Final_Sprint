const request = require("supertest");
const express = require("express");

const { BinarySearchTree } = require("../services/binarysearch.js");
const { logTree } = require("../logging/log-tree.js");

const app = express();
const treeifyRouter = require("../routes/treeify"); //Require search.js in routes folder and assign to the constant searchRouter.

app.use("/treeify", treeifyRouter); //Use searchRouter when /search route is called.

describe("GET /treeify", () => {
	test("should respond with status 400 and error message when query parameter is missing", async () => {
		const response = await request(app).get("/treeify");

		expect(response.body).toEqual(
			"400: Bad Request - Missing Query Parameter (Format: ?numbers=1,2,3,4,5)"
		);
	});

	test("should respond with status 400 and error message when query parameter is invalid", async () => {
		const response = await request(app)
			.get("/treeify?numbers=1,2,3,4,a")
			.expect(400);

		expect(response.body).toEqual(
			"400: Bad Request - Invalid Query Parameter. Must be integers separated by a comma (Format: ?numbers=1,2,3,4,5)"
		);
	});

	test("should respond with status 200 and tree data when query parameter is valid", async () => {
		const response = await request(app)
			.get("/treeify?numbers=1,2,3,4,5")
			.expect(200);

		expect(response.body).toEqual(
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
});

describe("GET /treeify/logs", () => {
	test("should respond with status 200 and log data with the last item in the log being the expected tree data ", async () => {
		let bst = new BinarySearchTree();
		bst.insertNode(1);
		bst.insertNode(2);
		bst.insertNode(3);
		bst.insertNode(4);
		bst.insertNode(5);
		const treeData = bst.root;

		await logTree(treeData);

		const response = await request(app).get("/treeify/logs").expect(200);
		let lastLogItem = response.body[response.body.length - 1];

		expect(lastLogItem).toEqual(
			expect.objectContaining({
				treedata: expect.objectContaining({
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
				}),
			})
		);
	});
});
