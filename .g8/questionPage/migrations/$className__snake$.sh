#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /$className;format="decap"$                       controllers.$className$Controller.onPageLoad()" >> ../conf/app.routes
echo "POST       /$className;format="decap"$                       controllers.$className$Controller.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.field1 = Field 1" >> ../conf/messages.en
echo "$className;format="decap"$.field2 = Field 2" >> ../conf/messages.en
echo "$className;format="decap"$.error.field1.required = Enter field1" >> ../conf/messages.en
echo "$className;format="decap"$.error.field2.required = Enter field2" >> ../conf/messages.en
echo "$className;format="decap"$.error.field1.length = field1 must be $field1MaxLength$ characters or less" >> ../conf/messages.en
echo "$className;format="decap"$.error.field2.length = field2 must be $field2MaxLength$ characters or less" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def $className;format="decap"$: Option[$className$] = cacheMap.getEntry[$className$]($className$Id.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $className;format="snake"$ completed"
