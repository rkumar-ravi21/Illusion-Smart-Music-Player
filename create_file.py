import csv

class create:

	@staticmethod
	def create_csv(data):
		data = data['data']
		fields = ['body_acc_x', 'body_acc_y', 'body_acc_z', 'gravity_acc_x', 'gravity_acc_y', 'gravity_acc_z', 'body_gyro_x', 'body_gyro_y', 'body_gyro_z']
		rows = []
		
		heart_rate = data[0]
		heart_rate = heart_rate['heartRate']
		heart_rate = heart_rate[0]
		data.pop(0)

		for val in data:
			temp_list = []
			temp_list.append(val['accelerometerX'])
			temp_list.append(val['accelerometerY'])
			temp_list.append(val['accelerometerZ'])
			temp_list.append(val['gravityX'])
			temp_list.append(val['gravityY'])
			temp_list.append(val['gravityZ'])
			temp_list.append(val['gyroscopeX'])
			temp_list.append(val['gyroscopeY'])
			temp_list.append(val['gyroscopeZ'])
			rows.append(temp_list)

		f = open('accelerometer.csv', 'w')
		csv_writer = csv.writer(f)
		csv_writer.writerow(fields)
		csv_writer.writerows(rows)
		f.close()
		print("CSV File Created")
		f = open('heart_rate.txt', 'w')
		f.write(heart_rate)
		f.close()
		print("Heart Rate File Created")
