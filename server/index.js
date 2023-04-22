if (process.env.NODE_ENV !== "production") {
	require("dotenv").config(); //Configure dotenv.
}

const express = require("express"); //Require express and assign it to the constant express.
const app = express();

const PORT = process.env.PORT || 3000; //Asssign the constant port to the port 3000.
global.DEBUG = true;

app.use(express.urlencoded({ extended: true })); //Set up urlencoded and extend it.

// localhost:3000/
app.get("/", function (req, res) {
	res
		.status(200)
		.json(
			"To use this API, please use the /treeify route with its 'numbers' query parameter (Format: /treeify?numbers=1,2,3,4,5)"
		);
});

// localhost:3000/treeify?numbers=1,2,3,4,5
// localhost:3000/treeify/logs
const treeifyRouter = require("./routes/treeify"); //Require search.js in routes folder and assign to the constant searchRouter.
app.use("/treeify", treeifyRouter); //Use searchRouter when /search route is called.

// Invalid Route/404
app.get("*", function (req, res) {
	res
		.status(404)
		.json(
			"404: Invalid route. To use this API, please use the /treeify route with its 'numbers' query parameter. (Format: /treeify?numbers=1,2,3,4,5)"
		);
});

app.listen(PORT, () => {
	//Set app to listening on port.
	console.log(`Simple app running on port ${PORT}.`);
});
