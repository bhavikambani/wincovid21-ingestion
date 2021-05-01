%define __jar_repack 0
Name: covimyn-ingestion-app
Version: 1.0.1
Release: SNAPSHOT20210501052840350_1
Summary: covimyn-ingestion
License: 2012, Myntra Designs Pvt Ltd
Group: Applications/Programming
autoprov: yes
autoreq: yes
BuildArch: noarch
BuildRoot: /Users/300004204/Work/WorkSpace/CovidProject2021/CovimynIngestion/covimyn-ingestion/target/rpm/covimyn-ingestion-app/buildroot

%description
Covid Myntra : Ingestion

%install
if [ -d $RPM_BUILD_ROOT ];
then
  mv /Users/300004204/Work/WorkSpace/CovidProject2021/CovimynIngestion/covimyn-ingestion/target/rpm/covimyn-ingestion-app/tmp-buildroot/* $RPM_BUILD_ROOT
else
  mv /Users/300004204/Work/WorkSpace/CovidProject2021/CovimynIngestion/covimyn-ingestion/target/rpm/covimyn-ingestion-app/tmp-buildroot $RPM_BUILD_ROOT
fi

%files

%attr(755,myntra,myntra) "/myntra/ingestion/releases/1.0.1-SNAPSHOT20210501052840350_1/bin"
%attr(755,myntra,myntra) "/myntra/ingestion/releases/1.0.1-SNAPSHOT20210501052840350_1/webapps/"
