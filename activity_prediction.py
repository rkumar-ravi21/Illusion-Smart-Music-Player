import numpy as np
import pandas as pd
import pickle


from scipy.stats import median_absolute_deviation
from scipy.stats import iqr
from scipy.stats import entropy

#import keras, keras_applications, keras_preprocessing
#from keras.models import load_model
from tensorflow import keras


class Activity:

	@staticmethod
	def jerk_values(data):
		d = data.iloc[:, 0:1].values
		l = []
		for i in range( 1, len(d) ):
			x = d[i-1] - d[i] #abs(d[i-1] - d[i])
			l.append(x[0])
		l.append(0.0)
		data["body_acc_jerk_x"] = l

		d = data.iloc[:, 1:2].values
		l = []
		for i in range( 1, len(d) ):
			x = d[i-1] - d[i] #abs(d[i-1] - d[i])
			l.append(x[0])
		l.append(0.0)
		data["body_acc_jerk_y"] = l

		d = data.iloc[:, 2:3].values
		l = []
		for i in range( 1, len(d) ):
			x = d[i-1] - d[i] #abs(d[i-1] - d[i])
			l.append(x[0])
		l.append(0.0)
		data["body_acc_jerk_z"] = l

		d = data.iloc[:, 6:7].values
		l = []
		for i in range( 1, len(d) ):
			x = d[i-1] - d[i] #abs(d[i-1] - d[i])
			l.append(x[0])
		l.append(0.0)
		data["body_gyro_jerk_x"] = l

		d = data.iloc[:, 7:8].values
		l = []
		for i in range( 1, len(d) ):
			x = d[i-1] - d[i] #abs(d[i-1] - d[i])
			l.append(x[0])
		l.append(0.0)
		data["body_gyro_jerk_y"] = l

		d = data.iloc[:, 8:9].values
		l = []
		for i in range( 1, len(d) ):
			x = d[i-1] - d[i] #abs(d[i-1] - d[i])
			l.append(x[0])
		l.append(0.0)
		data["body_gyro_jerk_z"] = l

		return data

	#Creating Magnitude Values
	@staticmethod
	def mag_values(data):
		d = data.iloc[:, 0:3].values
		lem = d.shape
		lem = lem[0]
		l = []
		for i in range(lem):
			x = d[i]
			l.append( np.linalg.norm(x) )
		data["body_acc_mag"] = l

		d = data.iloc[:, 3:6].values
		lem = d.shape
		lem = lem[0]
		l = []
		for i in range(lem):
			x = d[i]
			l.append( np.linalg.norm(x) )
		data["gravity_acc_mag"] = l

		d = data.iloc[:, 6:9].values
		lem = d.shape
		lem = lem[0]
		l = []
		for i in range(lem):
			x = d[i]
			l.append( np.linalg.norm(x) )
		data["body_gyro_mag"] = l

		d = data.iloc[:, 9:12].values
		lem = d.shape
		lem = lem[0]
		l = []
		for i in range(lem):
			x = d[i]
			l.append( np.linalg.norm(x) )
		data["body_acc_jerk_mag"] = l

		d = data.iloc[:, 12:15].values
		lem = d.shape
		lem = lem[0]
		l = []
		for i in range(lem):
			x = d[i]
			l.append( np.linalg.norm(x) )
		data["body_gyro_jerk_mag"] = l

		return data

	#Creating Frequency Domain Values
	@staticmethod
	def freq_domain_values(data):
		d = data.iloc[:, 0:1].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_x"] = l

		d = data.iloc[:, 1:2].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_y"] = l

		d = data.iloc[:, 2:3].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_z"] = l

		d = data.iloc[:, 6:7].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_gyro_x"] = l

		d = data.iloc[:, 7:8].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_gyro_y"] = l

		d = data.iloc[:, 8:9].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_gyro_z"] = l

		d = data.iloc[:, 9:10].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_jerk_x"] = l

		d = data.iloc[:, 10:11].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_jerk_y"] = l

		d = data.iloc[:, 11:12].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_jerk_z"] = l

		d = data.iloc[:, 15:16].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_mag"] = l

		d = data.iloc[:, 17:18].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_gyro_mag"] = l

		d = data.iloc[:, 18:19].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_acc_jerk_mag"] = l

		d = data.iloc[:, 19:20].values
		l = []
		x = np.fft.fft(d)
		for i in x:
			y = i[0]
			l.append(y.real)
		data["f_body_gyro_jerk_mag"] = l

		return data

	#Creating list for predicting
	@staticmethod
	def list_creation(data):
		l = []
		lem = data.shape
		lem = lem[1]
		for i in range(lem):
			x = data.iloc[:, i:i+1].values
			l.append( np.mean(x) )
			l.append( np.std(x) )
			y = median_absolute_deviation(x)
			l.append( y[0] )
			l.append( np.amax(x) )
			l.append( np.amin(x) )
			l.append( iqr(x) )
			y = np.absolute(x)
			y = entropy(y)
			l.append( y[0] )

		return l

	#Predicting and Saving Activity Detail
	@staticmethod
	def predict_and_save(l):
		saved_model = open('Activity.pickle', 'rb')
		classifier_model = pickle.load(saved_model)
		test_data = np.asarray(l)
		test_data = np.reshape(test_data, (1,-1))
		predictions = classifier_model.predict_classes(test_data)
		x = predictions[0]
		print("Activity Number: "+ str(x))
		f = open("activity_number.txt", "w")
		f.write(str(x))
		f.close()
		print("Activity File Created")

	@staticmethod
	def run():
		data = pd.read_csv("accelerometer.csv")
		data = Activity.jerk_values(data)
		data = Activity.mag_values(data)
		data = Activity.freq_domain_values(data)
		prediction_list = Activity.list_creation(data)
		Activity.predict_and_save(prediction_list)

#Activity.run()
