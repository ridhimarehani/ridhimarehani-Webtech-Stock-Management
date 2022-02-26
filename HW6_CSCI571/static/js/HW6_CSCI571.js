function searchCompany() {
    //let data;
    let companyName
    //let chart; // globally available
    company_name = document.getElementById('stock_search_input').value;
    console.log('ssssss> ' + company_name);
    if (company_name === '') {
        //Show Warning Message
        document.querySelector('#company_info').style.display = 'none';
    }
    else {
        //fetch stock search API
        fetch(`https://csci571rrehani.wl.r.appspot.com/stock_search?company_name=${company_name}`)
            .then(response => response.json())
            .then(res => {
                // console.log('checkError>' + res);
                if (Object.keys(res).length === 0) {
                    document.querySelector('.error_company').style.display = 'flex';
                    document.querySelector('#company_info').style.display = 'none';
                    refreshTabs();

                }
                else {
                    document.querySelector('.error_company').style.display = 'none';
                    let data = res;
                    let imgTag = `<br>`;
                    if (data.logo.length !== 0 && data.logo != null) {
                        imgTag = `<img src="${data.logo}" id="comapny_logo" alt="" height="100px" width="100px">`

                    }
                    document.querySelector('#company_logo_valid').innerHTML = imgTag;
                    document.getElementById("comp_name").innerHTML = data.name;
                    document.getElementById("stock_tick_sym").innerHTML = data.ticker;
                    companyName = data.ticker;
                    document.getElementById("stock_ex_code").innerHTML = data.exchange;
                    document.getElementById("ipo_date").innerHTML = data.ipo;
                    document.getElementById("category").innerHTML = data.finnhubIndustry;

                    var companyInfo = document.getElementById("company_info");
                    companyInfo.style.display = "block";

                }

            });

        fetch(`https://csci571rrehani.wl.r.appspot.com/stock_summary?company_name=${company_name}`)
            .then(response => response.json())
            .then(res => {
                dataStock = res.data_stock;
                //Setting Arrows for Postive and Negative Values Start
                let arrowForChange = '', arrowForPercentData = '';
                if (dataStock.d < 0) {
                    arrowForChange = "../static/image/RedArrowDown.png";
                }
                else if (dataStock.d > 0) {
                    arrowForChange = "../static/image/GreenArrowUp.png";
                }

                if (dataStock.dp < 0) {
                    arrowForPercentData = "../static/image/RedArrowDown.png";
                }
                else if (dataStock.dp > 0) {
                    arrowForPercentData = "../static/image/GreenArrowUp.png";
                }
                //Setting Arrows for Postive and Negative Values End

                let changeData = `${dataStock.d} <span><img src="${arrowForChange}" height = "12" width="12"></span>`;
                let changePercentData = `${dataStock.dp} <span><img src="${arrowForPercentData}" height = "12" width="12"></span>`;
                convertedTime = convertTime(dataStock.t)
                document.getElementById("stock_ticker_symbol").innerHTML = companyName;
                document.getElementById("trading_day").innerHTML = convertedTime;
                document.getElementById("previous_closing_price").innerHTML = dataStock.pc;
                document.getElementById("opening_price").innerHTML = dataStock.o;
                document.getElementById("high_price").innerHTML = dataStock.h;
                document.getElementById("low_price").innerHTML = dataStock.l;
                document.getElementById("change").innerHTML = changeData;
                document.getElementById("change_percent").innerHTML = changePercentData;

                dataRecommend = res.data_recommend;

                let strongSellVal = 'NA';
                let sellVal = 'NA';
                let holdVal = 'NA';
                let buyVal = 'NA';
                let strongBuyVal = 'NA';
                if (dataRecommend.length !== 0) {
                    strongSellVal = dataRecommend[0].strongSell;
                    sellVal = dataRecommend[0].sell;
                    holdVal = dataRecommend[0].hold;
                    buyVal = dataRecommend[0].buy;
                    strongBuyVal = dataRecommend[0].strongBuy;
                }
                document.getElementById("recommend_val1").innerHTML = strongSellVal;
                document.getElementById("recommend_val2").innerHTML = sellVal;
                document.getElementById("recommend_val3").innerHTML = holdVal;
                document.getElementById("recommend_val4").innerHTML = buyVal;
                document.getElementById("recommend_val5").innerHTML = strongBuyVal;


            });

        fetch(`https://csci571rrehani.wl.r.appspot.com/stock_charts?company_name=${company_name}`)
            .then(response => response.json())
            .then(res => {

                //Chart Data Logic Start
                let data_charts = res.chart_vals;
                let from_date = convertTime1(res.chart_from_date);
                chartData1 = []
                volumeData1 = []
                dataLen = data_charts.c.length

                for (let i = 0; i < dataLen; i += 1) {
                    let tempVar1 = []
                    let tempVar2 = []
                    tempVar1.push(data_charts.t[i] * 1000);
                    tempVar1.push(data_charts.c[i]);

                    chartData1.push(tempVar1)
                    tempVar2.push(data_charts.t[i] * 1000);
                    tempVar2.push(data_charts.v[i]);

                    volumeData1.push(tempVar2)
                }

                // Create the chart
                chartTitle = 'Stock Price ' + company_name + ' ' + from_date;
                Highcharts.stockChart('chart_container', {

                    rangeSelector: {
                        selected: 0,
                        inputEnabled: false,
                        buttons: [{
                            type: 'day',
                            count: 7,
                            text: '7d'
                        }, {
                            type: 'day',
                            count: 15,
                            text: '15d'
                        }, {
                            type: 'month',
                            count: 1,
                            text: '1m'
                        }, {
                            type: 'month',
                            count: 3,
                            text: '3m'
                        }, {
                            type: 'month',
                            count: 6,
                            text: '6m'
                        }],
                    },

                    title: {
                        text: chartTitle
                    },

                    subtitle: {
                        text: '<a href="https://finnhub.io/" target="_blank" >Source: Finnhub</a>',
                        useHTML: true
                    },

                    yAxis: [{
                        title: {
                            text: 'Stock Price'
                        },
                        opposite: false
                    },
                    {
                        labels: {
                            align: 'left'
                        },
                        title: {
                            text: 'Volume'
                        },
                        opposite: true,
                    }
                    ],
                    plotOptions: {
                        column: {
                            pointWidth: 5,
                        }
                    },

                    series: [{
                        name: 'Stock',
                        data: chartData1,
                        type: 'area',
                        yAxis: 0,
                        showInNavigator: true,
                        threshold: null,
                        tooltip: {
                            valueDecimals: 2
                        },
                        fillColor: {
                            linearGradient: {
                                x1: 0,
                                y1: 0,
                                x2: 0,
                                y2: 1
                            },
                            stops: [
                                [0, Highcharts.getOptions().colors[0]],
                                [1, Highcharts.color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                            ]
                        }
                    },
                    {
                        type: 'column',
                        name: 'Volume',
                        data: volumeData1,
                        yAxis: 1,
                        showInNavigator: false
                    }
                    ]
                });
            });
        //Dummy chart end

        fetch(`https://csci571rrehani.wl.r.appspot.com/stock_news?company_name=${company_name}`)
            .then(response => response.json())
            .then(res => {
                dataNews = res.dataNews;
                let newsContent = '';
                for (let data of dataNews) {
                    let convertedTime = convertTime(data.datetime)
                    newsContent += `<div class="news_container">
                <div class="news_container_left">
                    <img src="${data.image}" alt="news Image" id="news_image" height="80" width="80">
                </div>
                <div class="news_container_right">
                    <div id = "news_headline">${data.headline}</div>
                    <div id = "news_date">${convertedTime}</div>
                    <a href = "${data.url}" id = "news_url" target="__blank__">See Original Post</a>
                </div>
            </div>`

                }
                document.getElementById("latest_news_tab").innerHTML = newsContent;
            });

    }
}

function convertTime(time) {
    let unixTime = new Date(time * 1000)
    let months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    let year = unixTime.getFullYear();
    let month = months[unixTime.getMonth()];
    let date = unixTime.getDate();
    let formattedTime = date + ' ' + month + ', ' + year;
    return formattedTime;
}

function convertTime1(time) {
    let unixTime = new Date(time * 1000)
    let year = unixTime.getFullYear();
    let month = unixTime.getMonth();
    let date = unixTime.getDate();
    let formattedTime = ''
    if (month < 10) {
        formattedTime = year + '-0' + month + '-' + date;
    } else {
        formattedTime = year + '-' + month + '-' + date;
    }

    return formattedTime;
}


function onCrossClick() {
    document.getElementById('stock_search_input').value = ''
    document.querySelector('.error_company').style.display = 'none';
    document.querySelector('#company_info').style.display = 'none';
    refreshTabs();


}

function refreshTabs() {
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    tablinks[0].className += " active";
    document.getElementById("company_info_tab").style.display = "block";
}

function openTab(evt, tabName) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(tabName).style.display = "flex";
    evt.currentTarget.className += " active";
}

function openStockSummaryTab(evt, tabName) {
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";

}