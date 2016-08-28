//
//  ViewController.swift
//  MiAplicacion
//
//  Created by Emanuel Andrada on 22/8/16.
//  Copyright © 2016 UTN. All rights reserved.
//

import UIKit

class MiViewController: UIViewController {

    @IBOutlet var label: UILabel!

    var tapCount = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = NSLocalizedString("¡Hola mundo!", comment: "es lo que muestro al comienzo")
    }

    @IBAction func tocoElBoton() {
        tapCount += 1
        label.text = "tocaste \(tapCount) veces"
    }
}
