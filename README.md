# Dağıtık Abonelik Sistemi (Distributed Subscriber Service)

## Proje Hakkında

Bu proje, birden fazla sunucudan kapasite verilerini toplayan ve bu verileri gerçek zamanlı olarak görselleştiren bir sistemdir. Sunuculardan alınan kapasite verileri belirli aralıklarla izlenir ve matplotlib kullanarak bir grafik üzerinde gösterilir. Bu sayede, sistem yöneticileri veya kullanıcılar, sunucuların kapasite durumlarını görsel olarak takip edebilir.

## Proje Yapısı

Proje, farklı bileşenlerden oluşur:

1. **Server1.java, Server2.java, Server3.java**: 
   - Bu dosyalar, üç ayrı sunucunun her birini temsil eder. Sunucular, belirli portlar üzerinden kapasite verilerini dinler ve yanıtlar gönderir.
   - Sunucular, iki ana talep türünü işler: `STRT` (başlat) ve `CPCTY` (kapasite durumu).
   
2. **Admin.rb**:
   - AdminClient sınıfı, sunuculardan kapasite verilerini alır ve yönetir. Ayrıca, sunuculara başlatma komutları gönderir ve her sunucudan kapasite verisi alır.
   
3. **Plotter.py**:
   - Bu dosya, matplotlib kullanarak bir grafik üzerinde sunuculardan gelen kapasite verilerini görselleştirir. Grafik gerçek zamanlı olarak güncellenir.

4. **Protobuf Tanımları (proto dosyaları)**:
   - **capacity.proto, configuration.proto, message.proto, subscriber.proto**: Bu dosyalar, tüm sistemde kullanılan veri formatlarını tanımlar. Protobuf, verilerin hızlı ve verimli bir şekilde iletilmesini sağlar.

## Kullanılan Teknolojiler ve Kütüphaneler

- **Protobuf (Protocol Buffers)**: 
   - Veri iletimi için kullanılan bir dil bağımsız, platform bağımsız seri hale getirme (serialization) mekanizmasıdır.
   - `.proto` dosyaları, verilerin nasıl yapılandırılacağını tanımlar. Sunucular, admin ve plotter sistemleri bu protokolü kullanarak veri alışverişi yapar.

- **Java (Server1.java, Server2.java, Server3.java)**: 
   - Sunucular Java dilinde yazılmıştır ve belirli portlar üzerinden gelen bağlantıları dinler.
   - Java `ServerSocket` sınıfı kullanılarak istemcilerle TCP bağlantısı kurulur.

- **Ruby (Admin.rb)**: 
   - Ruby, sunuculara komut göndermek, konfigürasyonları okumak ve kapasite verilerini almak için kullanılır. Bu dosya, sunuculara başlatma komutları gönderir ve her bir sunucudan kapasite verilerini alır.

- **Python (Plotter.py)**: 
   - **Matplotlib**: Grafik çizimi için kullanılır. Plotter.py, sunuculardan gelen verileri bir grafik üzerinde görselleştirir.
   - **threading**: Çoklu portlardan gelen verileri paralel olarak dinler ve işler.

- **Matplotlib**:
   - Grafik çizimi için kullanılan bir Python kütüphanesidir. Bu projede, sunuculardan alınan verileri görselleştirmek için kullanılır.

## Projeye Genel Bakış

1. **Sunucu (Server1, Server2, Server3)**:
   - Her sunucu, belirli bir port üzerinden gelen bağlantıları dinler. Sunucular, gelen `STRT` komutuna karşılık başlatma işlemi yapar ve `CPCTY` komutuna yanıt olarak kapasite durumu gönderir.
   - Sunucular belirli aralıklarla (5 saniyede bir) kapasite verilerini gönderir.

2. **Admin Client (admin.rb)**:
   - **Yönetici** tarafı, tüm sunucuları yönetir ve konfigürasyon bilgilerini okur. 
   - Sunuculardan kapasite verilerini talep eder ve bu verileri alır. Yönetici, ayrıca sunucuları başlatmak için gerekli komutları gönderir.

3. **Plotter (plotter.py)**:
   - Sunuculardan gelen kapasite verilerini sürekli olarak dinler.
   - Alınan kapasite bilgilerini **matplotlib** kullanarak bir grafik üzerinde görselleştirir. Grafik, zaman içinde değişen kapasite durumlarını gösterir.

4. **Protobuf Tanımları**:
   - **capacity.proto**: Sunuculardan alınan kapasite verilerinin formatını tanımlar.
   - **configuration.proto**: Sunucu başlatma ve durdurma komutlarını içerir.
   - **message.proto**: Sunuculara gönderilen mesajların formatını tanımlar.
   - **subscriber.proto**: Abone ile ilgili verileri içerir.

## Kurulum

### 1. **Protobuf Derleme**:
   Projeyi kullanabilmek için önce `.proto` dosyalarının derlenmesi gerekir. Aşağıdaki komut kullanılarak protobuf dosyaları derlenebilir:
   
   ```bash
   protoc --python_out=. dist_servers.proto
   protoc --java_out=. dist_servers.proto
   protoc --ruby_out=. dist_servers.proto
   ```

 ### 2. **Java Sunucularını Çalıştırma**
 Sunuculara başlamak için Server1.java, Server2.java, Server3.java dosyalarını derlenip çalıştırılması gerekir:
 
  ```bash
  javac Server1.java Server2.java Server3.java
  java Server1
  java Server2
  java Server3
```
 ### 3. **Admin Client'ı Çalıştırma**
 Admin client, Ruby dilinde yazılmıştır. Ruby aşağıdaki komutla çalıştırılabiilir:
 ```bash
ruby admin.rb
```
### 4. **Plotter'ı Çalıştırma**
Python ve matplotlib kütüphanesi kullanılmıştır.Plotter aşağıdaki şekilde çalıştırılabilir:
```bash
python plotter.py
```
## Kullanım
- Admin Client çalıştırıldığında, sunuculardan kapasite verilerini talep eder.
- Sunucular, kapasite durumu verilerini belirli aralıklarla admin client'a gönderir.
- Plotter uygulaması, bu verileri dinler ve matplotlib kullanarak gerçek zamanlı bir grafik üzerinde gösterir.

## Yapı
```bash
/dist_servers
    ├── dist_servers.proto         # Protobuf tanımları
    ├── dist_subs.conf             # Yapılandırma dosyası
    ├── capacity.proto             # Kapasite verisi tanımları
    ├── configuration.proto        # Sunucu komutları tanımları
    ├── message.proto              # Mesaj formatları tanımları
    ├── subscriber.proto           # Abone bilgileri
    ├── Server1.java               # Sunucu 1
    ├── Server2.java               # Sunucu 2
    ├── Server3.java               # Sunucu 3
    ├── admin.rb                   # Yönetici istemcisi
    └── plotter.py                 # Kapasite verilerini görselleştiren Python scripti

```
EKİP ÜYELERİ
- Duygu KAYA
- Şule ŞAHAN
- Nagihan ZAN
- Nuray NART



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
