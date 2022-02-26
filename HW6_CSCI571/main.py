from crypt import methods
from itertools import count
import json
import requests
from flask import Flask,jsonify,request,render_template
from flask_cors import CORS, cross_origin
from datetime import date,timedelta,datetime
from dateutil.relativedelta import relativedelta, MO

app = Flask(__name__)
CORS(app,supports_credentials=True)
# class HW6_CSCI571:
@app.route('/')
def base_htmlpage_rendering():
    return render_template('index.html')
api_token = 'c83cnfqad3ift3bm6ub0'
company_name = ''



@app.route('/stock_search', methods = ['GET','POST'])
@cross_origin(supports_credentials=True)
def get_stock_search():
    company_name = request.args.get('company_name')
    data = requests.get(f'https://finnhub.io/api/v1/stock/profile2?symbol={company_name}&token={api_token}')
    return data.json()

@app.route('/stock_summary', methods = ['GET','POST'])
@cross_origin(supports_credentials=True)
def get_stock_summary():
    company_name = request.args.get('company_name')
    data_stock = requests.get(f'https://finnhub.io/api/v1/quote?symbol={company_name}&token={api_token}')
    data_recommend = requests.get(f'https://finnhub.io/api/v1/stock/recommendation?symbol={company_name}&token={api_token}')
    data_st = ({"data_stock" : data_stock.json(),
                "data_recommend" : data_recommend.json()})
    return jsonify(data_st)

@app.route('/stock_charts', methods = ['GET','POST'])
@cross_origin(supports_credentials=True)
def get_stock_charts():
    company_name = request.args.get('company_name')
    from_date = datetime.now() + relativedelta(months = -6 ,days =-1)
    to_date = datetime.now()
    from_date_unix = int(datetime.timestamp(from_date))
    to_data_unix = int(datetime.timestamp(to_date))
    data_charts = requests.get(f'https://finnhub.io/api/v1/stock/candle?symbol={company_name}&resolution=D&from={from_date_unix}&to={to_data_unix}&token={api_token}')
    data_charts_ret = ({"chart_vals" : data_charts.json(),
    "chart_from_date" : from_date_unix})
    return data_charts_ret

@app.route('/stock_news', methods = ['GET','POST'])
@cross_origin(supports_credentials=True)
def get_stock_news():
    data_news_res = []
    company_name = request.args.get('company_name')
    from_date = date.today() - timedelta(30)
    to_date = date.today()
    data_news = (requests.get(f'https://finnhub.io/api/v1/company-news?symbol={company_name}&from={from_date}&to={to_date}&token={api_token}'))
    if data_news.json() != []:
        data_news_json = json.loads(data_news.text)
        
        counter1 = 0
        counter2 = 0
        while(counter1 < 5):
            if(data_news_json[counter2]['image'] != '' and 
            data_news_json[counter2]['url'] != '' and
            data_news_json[counter2]['headline'] != '' and
            (data_news_json[counter2]['datetime'] != '' or data_news_json[counter2]['datetime'] != None)):
                data_news_res.append(data_news_json[counter2])
                counter1 += 1
            counter2 += 1
    return jsonify({'dataNews' : data_news_res})



if __name__ == "__main__":
    app.run(debug=True)

