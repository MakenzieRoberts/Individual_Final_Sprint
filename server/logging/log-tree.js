const { v4: uuid } = require("uuid");
const moment = require("moment");
const fs = require("fs");
const fsPromises = require("fs").promises;

// JSON formatter
var prettyjson = require("prettyjson");

const path = require("path");
const date = String(moment().format("ll"));
const time = String(moment().format("LTS"));
const month = moment().format("MMM");
const year = moment().year();

const logTree = async (treeData) => {
	// Creating the object that will be written to the log file
	const logItem = {
		date: date,
		time: time,
		treedata: treeData,
		uuid: uuid(),
	};

	// Path to the current dates folder that holds the log files
	let currentLogFolder = path.join(__dirname, `${month} ${year}_tree_logs`);

	try {
		// If the current date's folder does not exist then create it
		if (!fs.existsSync(path.join(currentLogFolder))) {
			await fsPromises.mkdir(path.join(currentLogFolder));
			console.log(`\nNew log folder created: '${month} ${year}_tree_logs'\n`);
		}

		// Current date's log file name
		const fileName = `${date}` + "_Tree_Log.json";

		// If the current date's log file exists, read existing logs
		let existingLogs;
		if (fs.existsSync(path.join(currentLogFolder, fileName))) {
			existingLogs = await fsPromises.readFile(
				path.join(currentLogFolder, fileName),
				"utf-8"
			);
		} else {
			console.log(`\nNew log file created: '${fileName}'\n`);
		}

		// If there are existing logs, parse them, else create an empty array to store new logs
		const logs = existingLogs ? JSON.parse(existingLogs) : [];

		// Push the new log item to the array
		logs.push(logItem);

		// Write the updated logs to the file
		await fsPromises.writeFile(
			path.join(currentLogFolder, fileName),
			JSON.stringify(logs)
		);

		console.log(
			`\nNew log added to '${fileName}': \n${prettyjson.render(logItem, {
				keysColor: "rainbow",
			})}\n`
		);
	} catch (err) {
		console.log(err);
	}
};

module.exports = { logTree };
