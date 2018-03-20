#!/bin/bash

echo "Applying migration UseOwnCar"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /useOwnCar                       controllers.UseOwnCarController.onPageLoad()" >> ../conf/app.routes
echo "POST       /useOwnCar                       controllers.UseOwnCarController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "useOwnCar.title = useOwnCar" >> ../conf/messages.en
echo "useOwnCar.heading = useOwnCar" >> ../conf/messages.en
echo "useOwnCar.error.required = Please give an answer for useOwnCar" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def useOwnCar: Option[Boolean] = cacheMap.getEntry[Boolean](UseOwnCarId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration UseOwnCar completed"
