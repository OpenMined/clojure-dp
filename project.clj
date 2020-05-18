(defproject differential-privacy-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "TODO"
            :url "TODO"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.google.protobuf/protobuf-java "3.11.4"]
                 [org.apache.commons/commons-math3 "3.6.1"]
                 [com.google.guava/guava "28.2-jre"]
                 [com.google.privacy.differentialprivacy/libdifferentialprivacy "1.0"]]
  :repl-options {:init-ns differential-privacy-clj.core})
