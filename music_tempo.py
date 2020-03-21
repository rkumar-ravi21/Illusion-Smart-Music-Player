import librosa
from glob import glob
import time


start_time = time.time()
data_directory = '/home/ravi/Ravi/Codes/Final_Year_Project/Illusion_Smart_Music_Player/DB'
audio_files = glob(data_directory + '/*.mp3')


music_path = audio_files[0].rpartition('/')[0] + "/"
music_data = {}
for i in audio_files:
	mn = i.rpartition('/')[-1]
	audio_time_series, sampling_rate = librosa.load(i)
	mt = librosa.beat.tempo(y = audio_time_series, sr = sampling_rate)
	music_data[mn] = mt[0]


audio_files = glob(data_directory + '/*.ogg')
for i in audio_files:
	mn = i.rpartition('/')[-1]
	audio_time_series, sampling_rate = librosa.load(i)
	mt = librosa.beat.tempo(y = audio_time_series, sr = sampling_rate)
	music_data[mn] = mt[0]


sorted_music_data = sorted(music_data.items(), key = lambda kv:(kv[1], kv[0]))
print("Music Path: "+ music_path+"\n")
f = open("music_data.txt", 'w')
f.write(music_path)
for i in sorted_music_data:
	f.write(i[0]+" "+str(i[1])+"\n")


print("Process Completed")
end_time = time.time()-start_time
print("Time: ", end_time/60)
f.close()






