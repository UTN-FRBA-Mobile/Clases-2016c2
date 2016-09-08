//
//  MiTablaViewController.swift
//  MiAplicacion
//
//  Created by Emanuel Andrada on 5/9/16.
//  Copyright © 2016 UTN. All rights reserved.
//

import UIKit

class MiTablaViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet var tableView: UITableView!

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 100
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Celda") as! MiTableViewCell
        cell.elLabel.text = "\(indexPath.row)"
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        NSLog("tocaron la celda \(indexPath.row)")
    }
}

class MiTableViewCell: UITableViewCell {
    
    @IBOutlet var elLabel: UILabel!
}
