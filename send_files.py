import librosa
from glob import glob
import activity_prediction



class send_music_files:

	@staticmethod
	def heart():
		f_heart_data = open("heart_rate.txt", 'r')
		x = f_heart_data.readline()
		x = x.split()
		heart_rate = int(x[0])
		f_heart_data.close()

		hr_code = 1
		if (heart_rate < 50):
			hr_code = 0
		elif (heart_rate >90):
			hr_code = 2
		else:
			hr_code = 1

		print("Heart Rate: "+ str(heart_rate))
		print("Heart Rate Code: "+ str(hr_code) )
		return hr_code

	@staticmethod
	def music():
		f_music_data = open("music_data.txt", 'r')
		music_path = f_music_data.readline()
		music_data = {}
		while(True):
			x = f_music_data.readline()
			if(not x):
				break
			x = x.split()
			music_data[x[0]] = float(x[1])
		f_music_data.close()
		return music_data, music_path

	@staticmethod
	def activity_number():
		activity_prediction.Activity.run()
		f_activity_data = open("activity_number.txt", "r+")
		n = f_activity_data.readline()
		n = int(n)
		#print("Activity Number: "+ str(n) )
		f_activity_data.close()
		return n

	@staticmethod
	def file_names(music_path, music_data, hr_code, a_n):
		f = []
		if( (hr_code==0 and a_n==0) or (hr_code==0 and a_n==1) or (hr_code==1 and a_n==0)):
			for name, hr in music_data.items():
				if(hr<100.0):
					f.append(music_path+name)

		elif( (hr_code==2 and a_n==1) or (hr_code==2 and a_n==2) ):
			for name, hr in music_data.items():
				if(hr>150.0):
					f.append(music_path+name)

		elif( (hr_code==0 and a_n==2) or (hr_code==1 and a_n==1) or (hr_code==1 and a_n==2) or (hr_code==2 and a_n==0) ):
			for name, hr in music_data.items():
				if(hr>=100.0 and hr<=150.0):
					f.append(music_path+name)

		else:
			print("Some Error occured.")

		return f

	@staticmethod
	def run():
		hr_code = send_music_files.heart()
		music_data, music_path = send_music_files.music()
		music_path = music_path.split()
		music_path = music_path[0]
		a_n = send_music_files.activity_number()
		#a_n = 1
		files = send_music_files.file_names(music_path, music_data, hr_code, a_n)
		return files

#smf = send_music_files()
#x = smf.run()
