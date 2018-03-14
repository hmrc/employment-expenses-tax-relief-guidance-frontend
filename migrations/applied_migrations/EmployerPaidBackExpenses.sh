#!/bin/bash

echo "Applying migration EmployerPaidBackExpenses"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employerPaidBackExpenses                       controllers.EmployerPaidBackExpensesController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employerPaidBackExpenses                       controllers.EmployerPaidBackExpensesController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "employerPaidBackExpenses.title = employerPaidBackExpenses" >> ../conf/messages.en
echo "employerPaidBackExpenses.heading = employerPaidBackExpenses" >> ../conf/messages.en
echo "employerPaidBackExpenses.error.required = Please give an answer for employerPaidBackExpenses" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def employerPaidBackExpenses: Option[Boolean] = cacheMap.getEntry[Boolean](EmployerPaidBackExpensesId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration EmployerPaidBackExpenses completed"
