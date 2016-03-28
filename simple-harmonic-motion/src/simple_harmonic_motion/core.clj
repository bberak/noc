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

(defn setup []
  (q/frame-rate 30)
  {:location (v/create 0 0)})

(defn update-state [{location :location}]
  (let [frame-count (q/frame-count)
        x (* frame-count speed)
        y (* amplitude (q/sin (* two-pi (/ frame-count period))))]
        {:location (v/create x y)}))

(defn draw-state [{location :location}]
  (q/background 240)
  (q/fill 0 0 0)
  (q/text (str "Period: " period " frames\nSpeed: " speed " pixels per frame\nFrequency: " frequency " waves per frame\nWavelength: " wavelength " pixels\nAmplitude: " amplitude) 10 50)
  (q/fill 0 255 255)
  (q/with-translation [0 (/ (q/height) 2)]
    (q/ellipse (:x location) (:y location) 10 10)))

(defn -main []
  (q/defsketch simple-harmonic-motion
    :title "Simple harmonic motion"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
