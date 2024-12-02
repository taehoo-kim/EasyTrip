import pandas as pd
from sklearn.model_selection import train_test_split
from catboost import CatBoostRegressor, Pool

df_place = pd.read_csv('tn_visit_area_info_방문지정보_A.csv')
df_travel = pd.read_csv('tn_travel_여행_A.csv')
df_traveler = pd.read_csv('tn_traveller_master_여행객 Master_A.csv')

df = pd.merge(df_place, df_travel, on='TRAVEL_ID', how='left')
df = pd.merge(df, df_traveler, on='TRAVELER_ID', how='left')

df_filter = df[~df['TRAVEL_MISSION_CHECK'].isnull()].copy()
df_filter.loc[:, 'TRAVEL_MISSION_INT'] = df_filter['TRAVEL_MISSION_CHECK'].str.split(';').str[0].astype(int)

df_filter = df_filter[[
    'GENDER',
    'AGE_GRP',
    'TRAVEL_STYL_1', 'TRAVEL_STYL_2', 'TRAVEL_STYL_3', 'TRAVEL_STYL_4', 'TRAVEL_STYL_5', 'TRAVEL_STYL_6', 'TRAVEL_STYL_7', 'TRAVEL_STYL_8',
    'TRAVEL_MOTIVE_1',
    'TRAVEL_COMPANIONS_NUM',
    'TRAVEL_MISSION_INT',
    'VISIT_AREA_NM',
    'DGSTFN',
]]

# df_filter.loc[:, 'GENDER'] = df_filter['GENDER'].map({'남': 0, '여': 1})

df_filter = df_filter.dropna()

categorical_features_names = [
    'GENDER',
    # 'AGE_GRP',
    'TRAVEL_STYL_1', 'TRAVEL_STYL_2', 'TRAVEL_STYL_3', 'TRAVEL_STYL_4', 'TRAVEL_STYL_5', 'TRAVEL_STYL_6', 'TRAVEL_STYL_7', 'TRAVEL_STYL_8',
    'TRAVEL_MOTIVE_1',
    # 'TRAVEL_COMPANIONS_NUM',
    'TRAVEL_MISSION_INT',
    'VISIT_AREA_NM',
    # 'DGSTFN',
]

df_filter[categorical_features_names[1:-1]] = df_filter[categorical_features_names[1:-1]].astype(int)

train_data, test_data = train_test_split(df_filter, test_size=0.2, random_state=42)

train_pool = Pool(train_data.drop(['DGSTFN'], axis=1),
                  label=train_data['DGSTFN'],
                  cat_features=categorical_features_names)

test_pool = Pool(test_data.drop(['DGSTFN'], axis=1),
                 label=test_data['DGSTFN'],
                 cat_features=categorical_features_names)

model = CatBoostRegressor(
    loss_function='RMSE',
    eval_metric='MAE',
    task_type='GPU',
    depth=6,
    learning_rate=0.01,
    n_estimators=2000)

model.fit(
    train_pool,
    eval_set=test_pool,
    verbose=500,
    plot=True)

model.predict(test_data.iloc[0].drop(['DGSTFN']))

model.get_feature_importance(prettified=True)
area_names = df_filter[['VISIT_AREA_NM']].drop_duplicates()

traveler = {
    'GENDER': '남',
    'AGE_GRP': 20.0,
    'TRAVEL_STYL_1': 1,
    'TRAVEL_STYL_2': 1,
    'TRAVEL_STYL_3': 2,
    'TRAVEL_STYL_4': 3,
    'TRAVEL_STYL_5': 2,
    'TRAVEL_STYL_6': 2,
    'TRAVEL_STYL_7': 2,
    'TRAVEL_STYL_8': 3,
    'TRAVEL_MOTIVE_1': 8,
    'TRAVEL_COMPANIONS_NUM': 0.0,
    'TRAVEL_MISSION_INT': 3,
}

results = pd.DataFrame([], columns=['AREA', 'SCORE'])

for area in area_names['VISIT_AREA_NM']:
    input = list(traveler.values())
    input.append(area)

    score = model.predict(input)

    results = pd.concat([results, pd.DataFrame([[area, score]], columns=['AREA', 'SCORE'])])

def Run():
    return results.sort_values('SCORE', ascending=False)['AREA'][:100].tolist()