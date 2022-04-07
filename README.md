# eureka-universe-api-v2
Eureka Universe Api for Eureka Universe Studio
# Tecnichal Documentation
https://www.bezkoder.com/spring-boot-jwt-auth-mongodb/
https://stackoverflow.com/questions/50545286/spring-boot-swagger-ui-set-jwt-token
https://stackoverflow.com/questions/62859164/unable-to-hit-controller-even-after-authentication-in-springdoc
# Servicio
copiar el archivo ``euapi.service`` a
``
/etc/systemd/system/euapi.service/etc/systemd/system/euapi.service
``

Posterior ejecutar
``$ systemctl start euapi``
y habilitar ``$ systemctl enable euapi``

Si aplicamos cambios al archivo euapi.service debemos de reiniciar el daemon
``systemctl daemon-reload``