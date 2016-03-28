(ns simple-harmonic-motion.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [simple-harmonic-motion.vectors :as v]
            [simple-harmonic-motion.forces :as f]))

(def two-pi (q/radians 360))
(def period 120) ;; In frames
(def speed 6) ;; Pixels per frame
(def frequency (/ 1 period)) ;; Waves per frame
(def wavelength (/ speed frequency)) ;; Pixels
(def amplitude 80)
(def samples-per-frame 1)

(defn setup []
  (q/frame-rate 30)
  {:frame 1})

(defn update-state [{frame :frame}]
  {:frame (inc frame)})

(defn draw-state [{samples :samples frame :frame}]
  (q/background 240)
  (q/fill 0 0 0)
  (q/text (str 
    "Period: " period " frames\n"
    "Speed: " speed " pixels per frame\n"
    "Frequency: " frequency " waves per frame\n"
    "Wavelength: " wavelength " pixels\n"
    "Amplitude: " amplitude "\n"
    "Samples: " samples-per-frame " samples per frame") 10 50)
  (q/with-translation [0 (/ (q/height) 2)]
    (let [start 0
          end (* frame speed)
          steps (* frame samples-per-frame)
          step-size (/ end steps)]
      (doseq [x (range start end step-size)]
        (q/point x (* amplitude (q/sin (* two-pi (/ (+ (* (/ x end) frame) frame) period)))))))))

(defn -main []
  (q/defsketch simple-harmonic-motion
    :title "Simple harmonic motion"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
