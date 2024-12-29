# Dağıtık Abonelik Sistemi (Distributed Subscriber Service)
##Proje Hakkında
Bu proje, birden fazla sunucudan kapasite verilerini toplayan ve bu verileri gerçek zamanlı olarak görselleştiren bir sistemdir. Sunuculardan alınan kapasite verileri belirli aralıklarla izlenir ve matplotlib kullanarak bir grafik üzerinde gösterilir. Bu sayede, sistem yöneticileri veya kullanıcılar, sunucuların kapasite durumlarını görsel olarak takip edebilir.

##Proje Yapısı
Proje, farklı bileşenlerden oluşur:

###Server1.java, Server2.java, Server3.java:

Bu dosyalar, üç ayrı sunucunun her birini temsil eder. Sunucular, belirli portlar üzerinden kapasite verilerini dinler ve yanıtlar gönderir.
Sunucular, iki ana talep türünü işler: STRT (başlat) ve CPCTY (kapasite durumu).
###Admin.rb:

AdminClient sınıfı, sunuculardan kapasite verilerini alır ve yönetir. Ayrıca, sunuculara başlatma komutları gönderir ve her sunucudan kapasite verisi alır.
###Plotter.py:

Bu dosya, matplotlib kullanarak bir grafik üzerinde sunuculardan gelen kapasite verilerini görselleştirir. Grafik gerçek zamanlı olarak güncellenir.
###Protobuf Tanımları (proto dosyaları):

capacity.proto, configuration.proto, message.proto, subscriber.proto: Bu dosyalar, tüm sistemde kullanılan veri formatlarını tanımlar. Protobuf, verilerin hızlı ve verimli bir şekilde iletilmesini sağlar.
Kullanılan Teknolojiler ve Kütüphaneler
###Protobuf (Protocol Buffers):

Veri iletimi için kullanılan bir dil bağımsız, platform bağımsız seri hale getirme (serialization) mekanizmasıdır.
.proto dosyaları, verilerin nasıl yapılandırılacağını tanımlar. Sunucular, admin ve plotter sistemleri bu protokolü kullanarak veri alışverişi yapar.
###Java (Server1.java, Server2.java, Server3.java):

Sunucular Java dilinde yazılmıştır ve belirli portlar üzerinden kapasite verilerini alır ve işler.
Java ServerSocket sınıfı kullanılarak istemcilerle TCP bağlantısı kurulur.
###Ruby (Admin.rb):

Ruby, sunuculara komut göndermek, konfigürasyonları okumak ve kapasite verilerini almak için kullanılır. Bu dosya, sunuculara başlatma komutları gönderir ve her bir sunucudan kapasite verilerini alır.
###Python (Plotter.py):

Matplotlib: Grafik çizimi için kullanılır. Plotter.py, sunuculardan gelen kapasite verilerini bir grafik üzerinde zaman içinde görselleştirir.
threading: Çoklu portlardan gelen verileri paralel olarak dinler ve işler.
###Matplotlib:

Grafik çizimi için kullanılan bir Python kütüphanesidir. Bu projede, sunuculardan alınan verileri görselleştirmek için kullanılır.
Projeye Genel Bakış
###Sunucu (Server1, Server2, Server3):

Her sunucu, belirli bir port üzerinden gelen bağlantıları dinler. Sunucular, gelen STRT komutuna karşılık başlatma işlemi yapar ve CPCTY komutuna yanıt olarak kapasite durumu gönderir.
Sunucular belirli aralıklarla (5 saniyede bir) kapasite verilerini gönderir.
###Admin Client (admin.rb):

Yönetici tarafı, tüm sunucuları yönetir ve konfigürasyon bilgilerini okur.
Sunuculardan kapasite verilerini talep eder ve bu verileri alır. Yönetici, ayrıca sunucuları başlatmak için gerekli komutları gönderir.
###Plotter (plotter.py):

Sunuculardan gelen kapasite verilerini sürekli olarak dinler.
Alınan kapasite bilgilerini matplotlib kullanarak bir grafik üzerinde görselleştirir. Grafik, zaman içinde değişen kapasite durumlarını gösterir.
###Protobuf Tanımları:

capacity.proto: Sunuculardan alınan kapasite verilerinin formatını tanımlar.
configuration.proto: Sunucu başlatma ve durdurma komutlarını içerir.
message.proto: Sunuculara gönderilen mesajların formatını tanımlar.
subscriber.proto: Abone ile ilgili verileri içerir. 


### plotter.py Solar System Exploration, 1950s – 1960s

- [ ] Mercury
- [x] Venus
- [x] Earth (Orbit/Moon)
- [x] Mars
- [ ] Jupiter
- [ ] Saturn
- [ ] Uranus
- [ ] Neptune
- [ ] Comet Haley

### admin.rb Solar System Exploration, 1950s – 1960s

- [ ] Mercury
- [x] Venus
- [x] Earth (Orbit/Moon)
- [x] Mars
- [ ] Jupiter
- [ ] Saturn
- [ ] Uranus
- [ ] Neptune
- [ ] Comet Haley

### ServerX.java Solar System Exploration, 1950s – 1960s

- [ ] Mercury
- [x] Venus
- [x] Earth (Orbit/Moon)
- [x] Mars
- [ ] Jupiter
- [ ] Saturn
- [ ] Uranus
- [ ] Neptune
- [ ] Comet Haley
