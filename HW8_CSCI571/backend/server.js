const express = require('express');
const app = express();
const port = 8080;
const request = require('request');
let axios = require('axios');
let api_token = 'c83cnfqad3ift3bm6ub0'
let cors = require('cors');
let ticker = '';
app.use(cors());
// app.use(cors({ origin: "http://localhost:8000", optionsSuccessStatus: 200 }));


app.get('/companyDescription', function (req, res) {
    // console.log('inside comaony Des');
    // api_token = request.arguments.get('ticker');
    // console.log('heyy>' + JSON.stringify(req.query));
    // console.log('heyy>' + req.query.ticker);
    axios.get(`https://finnhub.io/api/v1/stock/profile2?symbol=${req.query.ticker}&token=${api_token}`)
        .then(response => {
            // console.log(response)
            console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

//TBD : FROM and TO date, resolution=D
app.get('/historicalData', function (req, res) {
    //Lolly
    let date_today = new Date();
    let unix_curr = Math.floor(date_today / 1000);
    let backdate_2_years = date_today.setFullYear(date_today.getFullYear() - 2);
    let unix_backdate_2_years = Math.floor(backdate_2_years / 1000);
    console.log(unix_backdate_2_years);
    console.log(unix_curr);
    //Lolly
    axios.get(`https://finnhub.io/api/v1/stock/candle?symbol=${req.query.ticker}&resolution=D&from=${backdate_2_years}&to=${unix_curr}&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

//TBD : FROM and TO date, resolution=D
app.get('/historicalDataSummary', function (req, res) {
    //Lolly
    let date_today = new Date();
    let unix_curr = Math.floor(date_today / 1000);
    let backdate_2_years = date_today.setFullYear(date_today.getFullYear() - 2);
    let unix_backdate_2_years = Math.floor(backdate_2_years / 1000);
    console.log(unix_backdate_2_years);
    console.log(unix_curr);
    console.log('Lollyy>'+req.query.fromDate+'  '+req.query.toDate)
    //Lolly
    axios.get(`https://finnhub.io/api/v1/stock/candle?symbol=${req.query.ticker}&resolution=5&from=${req.query.fromDate}&to=${req.query.toDate}&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

// https://finnhub.io/api/v1/stock/candle?symbol=TSLA&resolution=D&from=1648735204&to=1648756804&token=c83cnfqad3ift3bm6ub0

app.get('/stockPrice', function (req, res) {
    axios.get(`https://finnhub.io/api/v1/quote?symbol=${req.query.ticker}&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

app.get('/autoComplete', function (req, res) {
    axios.get(`https://finnhub.io/api/v1/search?q=<QUERY>&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

//https://finnhub.io/api/v1/company-news?symbol=TSLA&from=2021-09-01&to=2021-09-09&token=c83cnfqad3ift3bm6ub0
//TBD : FROM and TO date
app.get('/companyNews', function (req, res) {
    let fromDate = '';
    let toDate = '';
    axios.get(`https://finnhub.io/api/v1/company-news?symbol=${req.query.ticker}&from=2021-09-01&to=2021-09-09&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});
//https://finnhub.io/api/v1/stock/recommendation?symbol=TSLA&token=c83cnfqad3ift3bm6ub0
app.get('/recommendation', function (req, res) {
    axios.get(`https://finnhub.io/api/v1/stock/recommendation?symbol=${req.query.ticker}&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

app.get('/socialSentiment', function (req, res) {
    axios.get(`https://finnhub.io/api/v1/stock/social-sentiment?symbol=${req.query.ticker}&from=2022-01-01&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log('socail Sentiment'+JSON.stringify(response.data));
            // console.log('----------');
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

app.get('/companyPeers', function (req, res) {
    axios.get(`https://finnhub.io/api/v1/stock/peers?symbol=${req.query.ticker}&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});

app.get('/companyEarnings', function (req, res) {
    axios.get(`https://finnhub.io/api/v1/stock/earnings?symbol=${req.query.ticker}&token=${api_token}`)
        .then(response => {
            // console.log(response)
            // console.log(response.data);
            res.send(response.data);
        })
        .catch(error => {
            console.log(error);
        });
});


// app.get('/route1', function (req, res) {
//     // request('https://finnhub.io/api/v1/search?q=TSLA&token='+api_token, { json: true }, (err, res, body) => {
//     axios.get(`https://finnhub.io/api/v1/search?q=${ticker}&token=${req.query.ticker}`)
//         .then(response => {
//             // console.log(response)
//             // console.log(response.data);
//             res.send(response.data);
//         })
//         .catch(error => {
//             console.log(error);
//         });
// });

// app.get('/route2', function (req, res) {
//     // request('https://finnhub.io/api/v1/search?q=TSLA&token='+api_token, { json: true }, (err, res, body) => {
//     axios.get(`https://finnhub.io/api/v1/stock/profile2?symbol=TSLA&token=${req.query.ticker}`)
//         .then(response => {
//             // console.log(response)
//             // console.log(response.data);
//             res.send(response.data);
//         })
//         .catch(error => {
//             console.log(error);
//         });
// });

app.listen(port, () => console.log(`Hello world app listening on port ${port}!`))
