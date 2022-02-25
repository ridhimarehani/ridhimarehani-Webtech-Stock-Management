function searchCompany() {
    let data;
    let companyName
    let chart; // globally available
    company_name = document.getElementById('stock_search_input').value;
    console.log('ssssss> ' + company_name);
    fetch(`http://127.0.0.1:5000/stock_search?company_name=${company_name}`)
        .then(response => response.json())
        .then(res => {
            console.log('checkError>' + res);
            if (company_name !== '') {
                if (Object.keys(res).length === 0) {
                    console.log('insodeIf')
                    document.querySelector('.error_company').style.display = 'flex';
                    document.querySelector('#company_info').style.display = 'none';
                   
                }
                else {
                    document.querySelector('.error_company').style.display = 'none';
                    data = res;
                    document.getElementById("comp_name").innerHTML = data.name;
                    document.getElementById("stock_tick_sym").innerHTML = data.ticker;
                    companyName = data.ticker;
                    document.getElementById("stock_ex_code").innerHTML = data.exchange;
                    document.getElementById("ipo_date").innerHTML = data.ipo;
                    document.getElementById("category").innerHTML = data.finnhubIndustry;

                    var companyInfo = document.getElementById("company_info");
                    companyInfo.style.display = "block";

                }
                 //Display None for error tooltip

            }
            // else{
            //     console.log('kkkk>');
            //     document.querySelector("#stock_search_input").setCustomValidity( "This username is not available" );
            //     // document.querySelector( ".search_image" ).click();
            // }

        });

    fetch(`http://127.0.0.1:5000/stock_summary?company_name=${company_name}`)
        .then(response => response.json())
        .then(res => {
            // console.log('data_stock>>> '+res.data_stock);
            dataStock = res.data_stock;
            document.getElementById("stock_ticker_symbol").innerHTML = companyName;
            document.getElementById("trading_day").innerHTML = dataStock.t; //TBD : Convert to human redable form
            document.getElementById("previous_closing_price").innerHTML = dataStock.pc;
            document.getElementById("opening_price").innerHTML = dataStock.o;
            document.getElementById("high_price").innerHTML = dataStock.h;
            document.getElementById("low_price").innerHTML = dataStock.l;
            document.getElementById("change").innerHTML = dataStock.d; //TBD : Display arrow
            document.getElementById("change_percent").innerHTML = dataStock.dp; //TBD : Display arrow
            dataRecommend = res.data_recommend;

            document.getElementById("recommend_val1").innerHTML = dataRecommend[0].strongSell;
            document.getElementById("recommend_val2").innerHTML = dataRecommend[0].sell;
            document.getElementById("recommend_val3").innerHTML = dataRecommend[0].hold;
            document.getElementById("recommend_val4").innerHTML = dataRecommend[0].buy;
            document.getElementById("recommend_val5").innerHTML = dataRecommend[0].strongBuy;


        });

    fetch(`http://127.0.0.1:5000/stock_charts?company_name=${company_name}`)
        .then(response => response.json())
        .then(res => {
            console.log('stock_charts>> ' + JSON.stringify(res));

            //Chart Data Logic Start
            data_charts = res
            console.log('len1> ' + data_charts.c.length)
            console.log('len2> ' + data_charts.t.length)
            console.log('len3> ' + data_charts.v.length)
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

            console.log('dataType> ', typeof (chartData1))
            console.log('dataaaaa>' + chartData1)
            console.log('dataType> ', typeof (volumeData1))
            console.log('dataqqqq>' + volumeData1)


            //Chart Data Logic End
            //Chart Data Start


            // let volume = []

            // for (let i = 0; i < volData.length; i += 1) {
            //     volume.push([
            //         volData[i][0], // the date
            //         volData[i][5] // the volume
            //     ]);
            // }

            // let groupingUnits = [[
            //     'week',                         // unit name
            //     [1]                             // allowed multiples
            // ], [
            //     'month',
            //     [1, 2, 3, 4, 6]
            // ]];


            //Chart Data End
            // //Charts Dummy Start
            // console.log('dataType> ', typeof (data))
            // console.log('data>' + data)
            // console.log('dataType> ', typeof (chartData))
            // console.log('data>' + chartData)
            // Create the chart
            Highcharts.stockChart('chart_container', {

                rangeSelector: {
                    selected: 1
                },

                title: {
                    text: 'AAPL Stock Price'
                },

                yAxis: [{
                    title: {
                        text: 'Stock Price'
                    },
                    opposite: false
                }, {
                    labels: {
                        align: 'right',
                        x: -3
                    },
                    title: {
                        text: 'Volume'
                    },
                    top: '65%',
                    height: '35%',
                    offset: 0,
                    lineWidth: 2,
                    opposite: true
                }],

                tooltip: {
                    split: true
                },

                series: [{
                    name: 'AAPL Stock Price',
                    data: chartData1,
                    type: 'area',
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
                    // dataGrouping: {
                    //     units: groupingUnits
                    // }
                }
                ]
            });
        });
    //Dummy chart end

    fetch(`http://127.0.0.1:5000/stock_news?company_name=${company_name}`)
        .then(response => response.json())
        .then(res => {
            dataNews = res.dataNews;
            let newsContent = '';
            for (let data of dataNews) {
                newsContent += `<div class="news_container">
                <div class="news_container_left">
                    <img src="${data.image}" alt="news Image" id="news_image" height="70" width="70">
                </div>
                <div class="news_container_right">
                    <div id = "news_headline">${data.headline}</div>
                    <div id = "news_date">${data.datetime}</div>
                    <a href = "${data.url}" id = "news_url" target="__blank__">See Original Post</a>
                </div>
            </div>`

            }
            document.getElementById("latest_news_tab").innerHTML = newsContent;
        });
}

function onCrossClick() {
    document.getElementById('stock_search_input').value = ''
    document.querySelector('.error_company').style.display = 'none';
    document.querySelector('#company_info').style.display = 'none';
}

function openTab(evt, cityName) {
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
    document.getElementById(cityName).style.display = "flex";
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