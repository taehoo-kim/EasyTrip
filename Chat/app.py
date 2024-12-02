from flask import Flask, request, jsonify
from openai import OpenAI
from datetime import datetime
import AI_Model as ai
import re

app = Flask(__name__)


def days_between_dates(date1, date2):
    # 문자열을 datetime 객체로 변환
    date1_obj = datetime.strptime(date1, '%Y-%m-%d')
    date2_obj = datetime.strptime(date2, '%Y-%m-%d')

    # 두 날짜 간의 차이 계산
    delta = date2_obj - date1_obj

    # 일 수 반환
    return abs(delta.days)


@app.route('/process', methods=['GET', 'POST'])
def process_question():
    try:
        data = request.get_json()
        question = data.get('question', '')

        Setting = ("입력된 문장에서 여행과 관련된 여행지, 예산, 인원, 일시 정보를 추출하여 Python list 형태로 반환하는 프롬포트"
                   )

        client = "api-key"
        print(question)

        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": Setting},
                {"role": "user", "content": question}
            ],
            temperature=0.2
        )

        travel_info = response.choices[0].message.content
        print(travel_info, "Travel_info")

        tmp = re.sub("(\'|\[|\]| )", "", travel_info)
        list_ans = tmp.split(',')
        print(list_ans, "List_ans")
        Spot = ai.Run()

        # 두 날짜 입력
        date1 = list_ans[3]
        date2 = list_ans[4]

        days = days_between_dates(date1, date2)

        # 여기까지가 정보 추출
        # 이제부터 여행 코스 추천
        post_response = []

        for i in range(4):
            Setting = (
                "[여행지, 예산, 인원, 여행 시작일, 여행 종료일] 정보와 여행할 장소를 입력받으면 이에 맞는 추천 여행 코스를 반환하는 프롬포트"
                "코스는 하루당 매우 자세하게 작성."
            )
            spots = Spot[i*days*2:(i+1)*days*2]
            question = travel_info + "\n" + ' '.join(spots) + "가 있는 지역으로 한정해줘"
            print(question, "Course Que")

            response = client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": Setting},
                    {"role": "user", "content": question}
                ],
                temperature=1
            )

            answer = response.choices[0].message.content
            post_response.append(answer)
            print(spots, "Spots")
            print(answer, "Ans", type(answer))
            print('------------------------------------------------------------------------------')

        return jsonify("////".join(post_response)), 200

    except Exception as e:
        print(e)
        response = {
            'error': str(e)
        }
        return jsonify(response), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)