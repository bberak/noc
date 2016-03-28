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
(def samples-per-frame 6)

(defn setup []
  (q/frame-rate 30)
  {:samples []
   :frame 0})

(defn update-state [{samples :samples frame :frame}]
  (let [start (* (dec frame) speed)
        end (+ start speed)
        sample-step (/ (- end start) samples-per-frame)
        sample-range (range start end sample-step)]
        {:samples (concat samples (map (fn [x] (v/create x (* amplitude (q/sin (* two-pi (/ x period)))))) sample-range))
         :frame (inc frame)}))

(defn draw-state [{samples :samples}]
  (q/background 240)
  (q/fill 0 0 0)
  (q/text (str 
    "Period: " period " frames\n"
    "Speed: " speed " pixels per frame\n"
    "Frequency: " frequency " waves per frame\n"
    "Wavelength: " wavelength " pixels\n"
    "Amplitude: " amplitude "\n"
    "Samples: " samples-per-frame " samples per frame") 10 50)
  (q/fill 0 255 255)
  (q/with-translation [0 (/ (q/height) 2)]
    (doseq [sample samples]
      (q/ellipse (:x sample) (:y sample) 10 10))))

(defn -main []
  (q/defsketch simple-harmonic-motion
    :title "Simple harmonic motion"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
