SET service.name=%1%

echo Creating profile for %service.name%

ant -f ant-deploy.xml -Dservice.name=%service.name% profile-build
