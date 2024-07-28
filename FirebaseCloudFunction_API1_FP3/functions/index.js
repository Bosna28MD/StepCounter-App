

/*

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");



var {google}=require("googleapis")
var MESSAGING_SCOPE="https://www.googleapis.com/auth/firebase.messaging";
var SCOPES=[MESSAGING_SCOPE];

var http=require("http");
var request=require("request");

var express=require("express");
var app=express();

var bodyParser=require("body-parser");
var router=express.Router();

app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json());


router.post("/send",(req,res)=>{
    

    getAccessToken().then((access_token)=>{
        var title=req.body.title;
        var body=req.body.body;
        var token=req.body.token;
        
        

        request.post({
            headers:{
                Authorization: "Bearer "+access_token
            },
            url: "https://fcm.googleapis.com/v1/projects/fp3-android/messages:send",
            body: JSON.stringify(
                {
                    "message":{
                        "token":token,
                        "notification":{
                            "body": body,
                            "title": title
                        }
                    }
                }
            )
        },(err,response,body)=>{
            res.end(body);
            console.log(body);
        });
        

    });

});

app.use("/api",router);



function getAccessToken(){
    return new Promise((res,rej)=>{
        var key=require("./server-account.json");
        var jwtClient=new google.auth.JWT(
            key.client_email,
            null,
            key.private_key,
            SCOPES,
            null
        );
        //console.log("key.email: "+key.client_email);
        //console.log("key.key: "+key.client_email);

        jwtClient.authorize((err,token)=>{
            if(err){
                rej(err);
                return;
            }

            res(token.access_token);
        })
    });
}





exports.api = onRequest(app);
*/
/*
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.newFunc123 = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase!");
});
*/

/*
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const request = require('request');
const { google } = require('googleapis');

const SCOPES = ['https://www.googleapis.com/auth/firebase.messaging'];

// Initialize the Firebase Admin SDK
admin.initializeApp();

function getAccessToken() {
    return new Promise((res, rej) => {
        var key = require("./server-account.json");
        var jwtClient = new google.auth.JWT(
            key.client_email,
            null,
            key.private_key,
            SCOPES,
            null
        );

        jwtClient.authorize((err, token) => {
            if (err) {
                rej(err);
                return;
            }

            res(token.access_token);
        });
    });
}

exports.sendMessage = functions.https.onRequest(async (req, res) => {
    const title = req.body.title;
    const body = req.body.body;
    const token = req.body.token;

    try {
        const accessToken = await getAccessToken();
        const options = {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json'
            },
            url: 'https://fcm.googleapis.com/v1/projects/fp3-android/messages:send',
            body: JSON.stringify({
                message: {
                    token: token,
                    notification: {
                        body: body,
                        title: title
                    }
                }
            })
        };

        request.post(options, (error, response, body) => {
            if (error) {
                console.error('Error sending message:', error);
                return res.status(500).send('Error sending message');
            }
            res.status(200).send('Message sent successfully');
        });
    } catch (err) {
        console.error('Error getting access token:', err);
        res.status(500).send('Error getting access token');
    }
});
*/

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const axios = require('axios');
const { google } = require('googleapis');

const SCOPES = ['https://www.googleapis.com/auth/firebase.messaging'];
const PATH_Server_Acount="./server-account.json";

// Initialize the Firebase Admin SDK
admin.initializeApp();

function getAccessToken() {
    return new Promise((res, rej) => {
        var key = require(PATH_Server_Acount);
        var jwtClient = new google.auth.JWT(
            key.client_email,
            null,
            key.private_key,
            SCOPES,
            null
        );

        jwtClient.authorize((err, token) => {
            if (err) {
                rej(err);
                return;
            }

            res(token.access_token);
        });
    });
}

exports.sendMessage = functions.https.onRequest(async (req, res) => {
    const key1 = req.body.key1;
    const key2 = req.body.key2;
    const token = req.body.token;
    const type_req="event_request";

    try {
        const accessToken = await getAccessToken();
        const response = await axios.post(
            'https://fcm.googleapis.com/v1/projects/fp3-android/messages:send',
            {
                message: {
                    token: token,
                    data: {
                        key1: key1,
                        key2: key2,
                        type_req:type_req
                    }
                }
            },
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                }
            }
        );
        res.status(200).send('Message sent successfully');
    } catch (err) {
        console.error('Error sending message:', err);
        res.status(500).send('Error sending message');
    }
});