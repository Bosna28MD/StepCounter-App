

/*

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const axios = require('axios');
const { google } = require('googleapis');
const SCOPES = ['https://www.googleapis.com/auth/firebase.messaging'];

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

exports.checkUsers = functions.https.onRequest(async (req, res) => {
    const key1 = req.body.key1;
    const key2 = req.body.key2;
    const type_req = "step_counter_register";

    try {
        const usersRef = admin.database().ref('users');
        const snapshot = await usersRef.once('value');
        const usersData = snapshot.val();
        const accessToken = await getAccessToken();

        const messages = [];
        for (const userUid in usersData) {
            const userData = usersData[userUid];
            const token = userData.token;
            if (token && token.trim() !== "") {
                messages.push({
                    token: token,
                    data: {
                        key1: key1,
                        key2: key2,
                        type_req: type_req
                    }
                });
            }
        }

        const responses = await Promise.all(messages.map(message =>
            axios.post(
                'https://fcm.googleapis.com/v1/projects/fp3-android/messages:send',
                { message },
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                        'Content-Type': 'application/json'
                    }
                }
            )
        ));

        res.status(200).send("Success");
    } catch (error) {
        console.error("Error checking user tokens:", error);
        res.status(500).send("Error checking user tokens");
    }
});

*/

const functions = require('firebase-functions');

const { onSchedule } = require('firebase-functions/v2/scheduler');
const admin = require('firebase-admin');
const axios = require('axios');
const { google } = require('googleapis');
const SCOPES = ['https://www.googleapis.com/auth/firebase.messaging'];
const PATH_Server_Acount="./server-account.json";

admin.initializeApp();

const key1 = 'exampleKey1';
const key2 = 'exampleKey2';
const type_req = 'step_counter_register';

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


exports.scheduleRetrieveSteps = onSchedule({
    schedule: '50 20 * * *'  // This cron expression runs the function every day,I set with 20:50 PM hour because my server is with 3 hours ahead and 20:50 -> 23:50
}, async (event) => {
    try {
        const usersRef = admin.database().ref('users');
        const snapshot = await usersRef.once('value');
        const usersData = snapshot.val();
        const accessToken = await getAccessToken();

        const messages = [];
        for (const userUid in usersData) {
            const userData = usersData[userUid];
            const token = userData.token;
            if (token && token.trim() !== "") {
                messages.push({
                    token: token,
                    data: {
                        //key1: key1,
                        //key2: key2,
                        type_req: type_req
                    }
                });
            }
        }

        const responses = await Promise.all(messages.map(message =>
            axios.post(
                'https://fcm.googleapis.com/v1/projects/fp3-android/messages:send',
                { message },
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                        'Content-Type': 'application/json'
                    }
                }
            )
        ));

        console.log("Success");
    } catch (error) {
        console.error("Error checking user tokens:", error);
    }
});