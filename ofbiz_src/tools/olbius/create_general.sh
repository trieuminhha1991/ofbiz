#! /bin/bash

UNIQUE_INSTANCEID=${UNIQUE_INSTANCEID:-"$(hostname)"}

cat > ${OLBIUS_PATH:-"../.."}/framework/common/config/general.properties << EOF

unique.instanceId=${UNIQUE_INSTANCEID}

currency.uom.id.default=${CURRENCY_UOM_ID_DEFAULT:-"VND"}

ORGANIZATION_PARTY=${ORGANIZATION_PARTY:-"company"}

VISUAL_THEME=${VISUAL_THEME:-"ACEADMIN"}

currency.decimal.format=${CURRENCY_DECIMAL_FORMAT:-"#0"}

currency.rounding.default=${CURRENCY_ROUNDING_DEFAULT:-10}

currency.scale.enabled=${CURRENCY_SCALE_ENABLED:-"N"}

locale.properties.fallback=${LOCALE_PROPERTIES_FALLBACK:-"vi"}

locales.available=${LOCALES_AVAILABLE:-"vi"}
locale.is.fixxed=${LOCALE_IS_FIXXED:-"true"}
locale.fixxed=${LOCALE_FIXXED:-"vi"}

country.geo.id.default=${COUNTRY_GEO_ID_DEFAULT:-"VNM"}

#countries.geo.id.available=USA

partner.trackingCodeId.default=${PARTNER_TRACKINGCODEID_DEFAULT:-""}

usps.address.match=(^.*?p[\\. ]*o[\\. ]*box.*$)|(^.*?post.*?office.*?box.*$)|((^|(^.*? ))r[\\. ]*r[\\. ]*(( +)|([0-9\#]+)).*$)|(^.*?rural.*?route.*$)

defaultFromEmailAddress=${DEFAULT_FROM_EMAIL_ADDRESS:-"contact@olbius.com"}

mail.notifications.enabled=${MAIL_NOTIFICATIONS_ENABLED:-"Y"}

#mail.notifications.redirectTo=

mail.smtp.relay.host=${MAIL_SMTP_RELAY_HOST:-"smtp.gmail.com"}

mail.smtp.auth.user=${MAIL_SMTP_AUTH_USER:-"olbiustest@gmail.com"}
mail.smtp.auth.password=${MAIL_SMTP_AUTH_PASSWORD:-"lbqiacdmftrmdiad"}

mail.smtp.port=${MAIL_SMTP_PORT:-465}
mail.smtp.starttls.enable=${MAIL_SMTP_STARTTLS_ENABLE:-"true"}

mail.smtp.socketFactory.port=${MAIL_SMTP_SOCKET_FACTORY_PORT:-465}
mail.smtp.socketFactory.class=${MAIL_SMTP_SOCKET_FACTORY_CLASS:-"javax.net.ssl.SSLSocketFactory"}
mail.smtp.socketFactory.fallback=${MAIL_SMTP_SOCKET_FACTORY_FALLBACK:-"false"}

mail.address.caseInsensitive=${MAIL_ADDRESS_CASE_INSENSITIVE:-"N"}

mail.debug.on=${MAIL_DEBUG_ON:-"N"}

mail.smtp.sendpartial=${MAIL_SMTP_SENDPARTIAL:-"true"}

http.upload.max.sizethreshold=${HTTP_UPLOAD_MAX_SIZETHRESHOLD:-10240}
http.upload.tmprepository=runtime/tmp
http.upload.max.size=${HTTP_UPLOAD_MAX_SIZE:-"-1"}

mail.spam.name=${MAIL_SPAM_NAME:-"X-Spam-Flag"}
mail.spam.value=${MAIL_SPAM_VALUE:-"YES"}

https.demo-trunk.ofbiz.apache.org=ABQIAAAAtt0d8djaYFkk8N5LJVcDSBTl26GJHIFzHZYG8GNWSTKWDUTxchRLjgT9hY3-DDYk27lvZS84RH4aiQ
https.demo-stable.ofbiz.apache.org=ABQIAAAAtt0d8djaYFkk8N5LJVcDSBR8L_-1UdAfCE2bleqTaEvMtKARZxSrkTzKktKY2_Znm0TRq2DF4YhGPg

https.localhost=ABQIAAAAtt0d8djaYFkk8N5LJVcDSBQN-clGH2vvMMwJjYtcwF78UzZgEBTN70S6uIgRoAtXRkADNoesbw5etg
http.localhost=ABQIAAAAtt0d8djaYFkk8N5LJVcDSBT2yXp_ZAY8_ufC3CFXhHIE1NvwkxR3euHYk9bpwvdF2Qg1EYO1LQitHA

multitenant=${MULTITENANT:-"N"}
tenantid=${DOMAIN_NAME}

notification.enable=${NOTIFICATION_ENABLE:-"Y"}
notification.limit=${NOTIFICATION_LIMIT:-10}

export.pdf.direct.size=${EXPORT_PDF_DIRECT_SIZE:-250000}

EOF
