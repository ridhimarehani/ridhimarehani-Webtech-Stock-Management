function searchCompany() {
    let data;
    let companyName
    company_name = document.getElementById('stock_search_input').value;
    console.log(company_name);
    fetch(`http://127.0.0.1:5000/stock_search?company_name=${company_name}`)
        .then(response => response.json())
        .then(res => {
            console.log(res);
            // data = res.stock_search_company;
            data = res;
            // document.getElementById("company_info").innerHTML = data.country;
            document.getElementById("comp_name").innerHTML = data.name;
            document.getElementById("stock_tick_sym").innerHTML = data.ticker;
            companyName = data.ticker;
            document.getElementById("stock_ex_code").innerHTML = data.exchange;
            document.getElementById("ipo_date").innerHTML = data.ipo;
            document.getElementById("category").innerHTML = data.finnhubIndustry;

            var companyInfo = document.getElementById("company_info");
            companyInfo.style.display = "block";

            // var companyInfoTab = document.getElementById("company_info_tab");
            // companyInfoTab.classList.add("active");
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
            console.log('stock_charts>> ' + res);
        });

    fetch(`http://127.0.0.1:5000/stock_news?company_name=${company_name}`)
        .then(response => response.json())
        .then(res => {
            dataNews = res.dataNews
            let newsContent
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

            // document.getElementById("news_image").src = dataNews[0].image;
            // document.getElementById("news_headline").innerHTML = dataNews[0].headline;
            // document.getElementById("news_date").innerHTML = dataNews[0].datetime;
            // document.getElementById("news_url").href = dataNews[0].url;

            //             for (let data of dataNews) {
            //                 // Elements inside News tab
            //                 const news_container = document.createElement("div");
            //                 const news_container_left = document.createElement("div");
            //                 const news_container_right = document.createElement("div");

            //                 news_container.setAttribute("id", "news_container");
            //                 news_container.className = "news_container";
            //                 news_container_left.setAttribute("id", "news_container_left");
            //                 news_container_left.className = "news_container_left";
            //                 news_container_right.setAttribute("id", "news_container_right");
            //                 news_container_right.className = "news_container_right";

            //                 news_container.appendChild(news_container_left);
            //                 news_container.appendChild(news_container_right);

            //                 // Elements inside news_container_left
            //                 const news_image = document.createElement("img");
            //                 news_image.setAttribute("id", "news_image");
            //                 news_image.setAttribute("height", "70");
            //                 news_image.setAttribute("width", "70");
            //                 console.log('imData> '+ dataNews[0].image)
            //                 news_image.setAttribute("src", data.image);
            // {/* <div class="news_container_right">
            //                     <div id="news_headline"></div>
            //                     <div id="news_date"></div>
            //                     <a id = "news_url" target="__blank__">See Original Post</a>
            //                 </div> */}

            //                 news_container_left.appendChild(news_image);

            //                 // Elements inside news_container_right
            //                 const  news_headline = document.createElement("div");
            //                 const  news_date = document.createElement("div");
            //                 const  news_url = document.createElement("a");
            //                 news_headline.id = "news_headline"
            //                 news_headline.textContent = data.headline
            //                 news_date.id = "news_date"
            //                 news_date.textContent = data.datetime
            //                 news_url.href = data.url
            //                 news_url.target = "_blank"
            //                 news_url.textContent = "See Original Post"

            //                 news_container_right.appendChild(news_headline);
            //                 news_container_right.appendChild(news_date);
            //                 news_container_right.appendChild(news_url);

            //                 document
            //                     .getElementById("latest_news_tab")
            //                     .appendChild(news_container);
            //             }
        });
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