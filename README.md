# high-traffix-board



# elastic, kibana 인증서 관련 사전 작업

### 0) 작업 디렉토리

```bash
mkdir -p certs
cd certs
```

---

### 1) CA 생성

```bash
openssl req -x509 -new -nodes -sha256 -days 3650 \
  -keyout ca.key \
  -out ca.crt \
  -subj "/CN=MyCA"
```

---

### 2) Elasticsearch 인증서 (SAN=localhost, elasticsearch)

### OpenSSL 설정 파일

```bash
cat > openssl-es.cnf <<'EOF'
[ req ]
default_bits       = 4096
distinguished_name = dn
req_extensions     = v3_req
prompt             = no

[ dn ]
CN = elasticsearch

[ v3_req ]
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = elasticsearch
DNS.2 = localhost
EOF
```

### 키/CSR/CRT 생성

```bash
openssl req -new -newkey rsa:4096 -nodes \
  -keyout es.key \
  -out es.csr \
  -config openssl-es.cnf

openssl x509 -req -in es.csr \
  -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out es.crt -days 365 -sha256 \
  -extfile openssl-es.cnf -extensions v3_req
```

---

### 3) Kibana 인증서 (SAN=localhost, elasticsearch)

### OpenSSL 설정 파일

```bash
cat > openssl-kibana.cnf <<'EOF'
[ req ]
default_bits       = 4096
distinguished_name = dn
req_extensions     = v3_req
prompt             = no

[ dn ]
CN = kibana

[ v3_req ]
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = kibana
DNS.2 = localhost
DNS.3 = elasticsearch
EOF
```

### 키/CSR/CRT 생성

```bash
openssl req -new -newkey rsa:4096 -nodes \
  -keyout kibana.key \
  -out kibana.csr \
  -config openssl-kibana.cnf

openssl x509 -req -in kibana.csr \
  -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out kibana.crt -days 365 -sha256 \
  -extfile openssl-kibana.cnf -extensions v3_req
```

### 4) kibana_system 유저 패스워드 재설정 - URL을 SAN에 맞게 강제



```bash
`localhost` 또는 `elasticsearch`로 붙게 `--url`을 지정해서 실행하세요. 둘 다 SAN에 있으니 성공합니다.

# ES 컨테이너 안에서 실행
bin/elasticsearch-reset-password -u kibana_system --url https://localhost:9200 -i
# 또는
bin/elasticsearch-reset-password -u kibana_system --url https://elasticsearch:9200 -i

```