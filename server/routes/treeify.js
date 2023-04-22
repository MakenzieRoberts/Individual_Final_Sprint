const express = require("express");
const router = express.Router();
const fs = require("fs");
var prettyjson = require("prettyjson");

const { BinarySearchTree } = require("../services/binarysearch.js");
const { logTree } = require("../logging/log-tree.js");

router.get("/", async function (req, res) {
	try {
		if (!req.query.numbers) {
			throw new Error(
				"400: Bad Request - Missing Query Parameter (Format: ?numbers=1,2,3,4,5)"
			);
		} else {
			const numbers = req.query.numbers;

			let splitArr = numbers.split(",");
			let bst = new BinarySearchTree();
			for (let i = 0; i < splitArr.length; i++) {
				if (isNaN(splitArr[i])) {
					res.status(400);
					throw new Error(
						"400: Bad Request - Invalid Query Parameter. Must be integers separated by a comma (Format: ?numbers=1,2,3,4,5)"
					);
				}
				splitArr[i] = parseInt(splitArr[i]);
				bst.insertNode(splitArr[i]);
			}
			const treeData = bst.root;

			console.log(
				`\nNew tree created:\n${prettyjson.render(treeData, {
					keysColor: "rainbow",
				})}\n`
			);
			await logTree(treeData);
			res.status(200).json(treeData);
		}
	} catch (e) {
		console.log(e);
		res.status(400).json(e.message);
	}
});

router.get("/logs", function (req, res) {
	let directory = "./logging/";
	try {
		let logFolders = fs
			.readdirSync(directory)
			.filter(
				(file) =>
					fs.statSync(`${directory}/${file}`).isDirectory() &&
					file.endsWith("_tree_logs")
			);

		const allLogs = [];

		logFolders.forEach((folder) => {
			const folderPath = `${directory}/${folder}`;
			const logFiles = fs
				.readdirSync(folderPath)
				.filter((file) => file.endsWith(".json"));
			const logs = [];

			logFiles.forEach((logFile) => {
				const logFilePath = `${folderPath}/${logFile}`;
				const data = fs.readFileSync(logFilePath);
				logs.push(JSON.parse(data));
			});
			for (let i = 0; i < logs.length; i++) {
				logs[i].forEach((log) => {
					allLogs.push(log);
				});
			}
		});

		res.json(allLogs);
	} catch (e) {
		console.log(e);
	}
});

module.exports = router;
