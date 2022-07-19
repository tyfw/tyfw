[1mdiff --git a/backend/package-lock.json b/backend/package-lock.json[m
[1mindex 662a812..72d5379 100644[m
[1m--- a/backend/package-lock.json[m
[1m+++ b/backend/package-lock.json[m
[36m@@ -13,6 +13,7 @@[m
         "dotenv": "^16.0.1",[m
         "express": "^4.18.1",[m
         "google-auth-library": "^8.0.2",[m
[32m+[m[32m        "graylog2": "^0.2.1",[m
         "mongodb": "^4.7.0",[m
         "web3": "^1.7.3"[m
       },[m
[36m@@ -3697,6 +3698,14 @@[m
       "resolved": "https://registry.npmjs.org/graceful-fs/-/graceful-fs-4.2.10.tgz",[m
       "integrity": "sha512-9ByhssR2fPVsNZj478qUUbKfmL0+t5BDVyjShtyZZLiK7ZDAArFFfopyOTj0M05wE2tJPisA4iTnnXl2YoPvOA=="[m
     },[m
[32m+[m[32m    "node_modules/graylog2": {[m
[32m+[m[32m      "version": "0.2.1",[m
[32m+[m[32m      "resolved": "https://registry.npmjs.org/graylog2/-/graylog2-0.2.1.tgz",[m
[32m+[m[32m      "integrity": "sha512-vjysakwOhrAqMeIvSK0WZcmzKvkpxY6pCfT9QqtdSVAidPFIynuin7adqbdFp9MCCTbTE402WIxvg8cph5OWTA==",[m
[32m+[m[32m      "engines": {[m
[32m+[m[32m        "node": ">=0.6.11"[m
[32m+[m[32m      }[m
[32m+[m[32m    },[m
     "node_modules/gtoken": {[m
       "version": "5.3.2",[m
       "resolved": "https://registry.npmjs.org/gtoken/-/gtoken-5.3.2.tgz",[m
[36m@@ -10664,6 +10673,11 @@[m
       "resolved": "https://registry.npmjs.org/graceful-fs/-/graceful-fs-4.2.10.tgz",[m
       "integrity": "sha512-9ByhssR2fPVsNZj478qUUbKfmL0+t5BDVyjShtyZZLiK7ZDAArFFfopyOTj0M05wE2tJPisA4iTnnXl2YoPvOA=="[m
     },[m
[32m+[m[32m    "graylog2": {[m
[32m+[m[32m      "version": "0.2.1",[m
[32m+[m[32m      "resolved": "https://registry.npmjs.org/graylog2/-/graylog2-0.2.1.tgz",[m
[32m+[m[32m      "integrity": "sha512-vjysakwOhrAqMeIvSK0WZcmzKvkpxY6pCfT9QqtdSVAidPFIynuin7adqbdFp9MCCTbTE402WIxvg8cph5OWTA=="[m
[32m+[m[32m    },[m
     "gtoken": {[m
       "version": "5.3.2",[m
       "resolved": "https://registry.npmjs.org/gtoken/-/gtoken-5.3.2.tgz",[m
[1mdiff --git a/backend/package.json b/backend/package.json[m
[1mindex 43395cb..8664ca4 100644[m
[1m--- a/backend/package.json[m
[1m+++ b/backend/package.json[m
[36m@@ -5,6 +5,7 @@[m
     "dotenv": "^16.0.1",[m
     "express": "^4.18.1",[m
     "google-auth-library": "^8.0.2",[m
[32m+[m[32m    "graylog2": "^0.2.1",[m
     "mongodb": "^4.7.0",[m
     "web3": "^1.7.3"[m
   },[m
[1mdiff --git a/backend/src/server.js b/backend/src/server.js[m
[1mindex a76599b..177215d 100644[m
[1m--- a/backend/src/server.js[m
[1m+++ b/backend/src/server.js[m
[36m@@ -41,7 +41,7 @@[m [masync function run() {[m
 [m
 // Google User Auth[m
 const {OAuth2Client, UserRefreshClient} = require('google-auth-library');[m
[31m-const { getBalance, getEthBalance } = require('./data.js');[m
[32m+[m[32mconst { getBalance, getEthBalance, getAccountHistory} = require('./data.js');[m
 const CLIENT_ID = process.env.CLIENT_ID;[m
 const client = new OAuth2Client(CLIENT_ID);[m
 [m
[36m@@ -149,7 +149,8 @@[m [mapp.get("/user/displaycurruser", async (req, res) => {[m
         throw new Error('No users found')[m
       }[m
       else {[m
[31m-        res.status(200).json(user.wallet)[m
[32m+[m[32m        const accountHistory = getAccountHistory(user.addresses[0])[m[41m [m
[32m+[m[32m        res.status(200).json(accountHistory)[m
       }[m
   }[m
   catch (err) {[m
