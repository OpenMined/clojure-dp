(defproject differential-privacy-clj "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://github.com/OpenMined/differential-privacy-clj"
  :license {:name "Apache License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.google.protobuf/protobuf-java "3.11.4"]
                 [org.apache.commons/commons-math3 "3.6.1"]
                 [com.google.guava/guava "28.2-jre"]
                 [com.google.privacy.differentialprivacy/libdifferentialprivacy "1.0"]]
  :repl-options {:init-ns differential-privacy-clj.core}

  ;; formatting:
  :cljfmt {:remove-consecutive-blank-lines? false})
