from openai import OpenAI

if __name__ == '__main__':

    Client = OpenAI(
        api_key="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"
    )
    Setting = ("여행에 대한 정보들을 입력받는 프롬포트."
               "여행지, 예산, 인원을 입력받는다."
               "첫 번째 줄에는 여행정보를 python list 형태로 출력"
               "두 번째 줄에는 사용자에게 전달할 답변"
               "입력 데이터가 부족하면 두 번째 줄 답변을 통해 요청"
               "입력이 완료되면 기존 출력에 더해 \'Finish\'를 출력"
               )

    lines = []
    client = OpenAI(
        api_key="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",
    )

    while(True):
        question = input(
               "입력 예시 : 서울, 100만원, 3명, 7/1 ~ 7/3\n" +
                "Ask\n> ")

        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages  = [
                {"role" : "system", "content" : Setting},
                {"role" : "user", "content" : question}
            ],
            temperature = 0.2
        )

        answer = response.choices[0].message.content
        print(answer)

        lines = answer.splitlines(True)
        if lines[-1] == "Finish":
            break

        if lines[-1][-6:] == "Finish":
            break

    print(lines[0])

    # 여기까지가 정보 추출
    # 이제부터 여행 코스 추천

    Setting = (
        "[여행지, 예산, 인원, 일정] 정보와 여행할 장소를 입력받으면 이에 맞는 추천 여행 코스를 반환하는 프롬포트"
        "코스는 하루당 매우 자세하게 작성."

    )
    spot = ["경복궁", "홍대", "명동식당"]
    question = lines[0] + "\n" + ' '.join(spot) + "가 있는 지역으로 한정해줘"

    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "system", "content": Setting},
            {"role": "user", "content": question}
        ],
        temperature= 1
    )

    answer = response.choices[0].message.content
    print(answer)