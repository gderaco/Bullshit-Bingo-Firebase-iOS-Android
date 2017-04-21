//
//  ViewController.swift
//  GoodbyeJeroen
//
//  Created by Giuseppe Deraco on 27/01/2017.
//  Copyright © 2017 Giuseppe Deraco. All rights reserved.
//

import UIKit
import JGProgressHUD
import Firebase
import FirebaseDatabase

class WordTableViewCell: UITableViewCell {
    @IBOutlet var nameLabel: UILabel?
    @IBOutlet var rankLabel: UILabel?
    @IBOutlet var incrementerUIButton: UIButton?
}

class Word {
    var name : String?
    var score : Int?
    
    init(name: String, score: Int) {
        self.name = name
        self.score = score
    }
}

class ViewController: UIViewController,UITableViewDataSource, UITableViewDelegate {
    
    @IBOutlet weak var tableview: UITableView!
    
    var words = Array<Word>()
    var ref: FIRDatabaseReference!
    var hud : JGProgressHUD!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        FIRApp.configure()
        ref = FIRDatabase.database().reference()
        
        hud = JGProgressHUD(style: .dark)
        
        getData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    //MARK: UITableViewDataSource
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return words.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "WordTableViewCell", for: indexPath) as! WordTableViewCell
        cell.nameLabel?.text = words[indexPath.row].name
        cell.rankLabel?.text = "\(words[indexPath.row].score!)"
        cell.incrementerUIButton?.tag = indexPath.row
        return cell
    }
    
    //MARK: UITableViewDelegate
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 70
    }
    
    @IBAction func incrementButtonHasBeenPressed(_ sender: UIButton) {
        let word = words[sender.tag]
        ref.child(word.name!).setValue(word.score! + 1)
        //getData()
    }
    
    func getData() -> Void
    {
        hud = JGProgressHUD(style: .dark)
        
        //showHud()
        
        ref.observe(FIRDataEventType.value, with: { (snapshot) in
            
            self.showHud()
            
            let wordsFromFirebase = snapshot.value as? NSDictionary
            
            if(wordsFromFirebase == nil || wordsFromFirebase!.count == 0) { return }
            
            self.words.removeAll()
            
            for (name, score) in wordsFromFirebase! {
                self.words.append(Word(name: name as! String, score: score as! Int))
            }
            
            self.words.sort(by: { (w1, w2) -> Bool in
                w1.score! > w2.score!
            })
            
            self.hideHUD()
        })
    }
    
    func addNewWord(word : Word) -> Void {
        
        if((word.name?.characters.count)! > 0)
        {
            let foundWord = words.filter{ $0.name == word.name! }.first
            
            if foundWord != nil {
                word.score! = (foundWord?.score!)! + 1
                ref.child(word.name!).setValue(word.score!)
            }
            else{//new word
                ref.child(word.name!).setValue(word.score)
            }
        }
    }
    
    //MARK: Events
    
    @IBAction func onAddButtonPressed(_ sender: Any) {
        let alertController = UIAlertController(title: "Voeg een woord toe", message: "Heeft Jeroen weer een mooi woord gebruikt? Zet 'm op de lijst!", preferredStyle: .alert)
        
        let saveAction = UIAlertAction(title: "Toevoegen", style: .default, handler: {
            alert -> Void in
            
            let firstTextField = alertController.textFields![0] as UITextField
            self.addNewWord(word: Word(name: firstTextField.text!, score: 1))
        })
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .default, handler: {
            (action : UIAlertAction!) -> Void in
        })
        
        alertController.addTextField { (textField : UITextField!) -> Void in
            textField.placeholder = "New Word"
        }
        
        alertController.addAction(saveAction)
        alertController.addAction(cancelAction)
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    //MARK: Hud
    func hideHUD() -> Void
    {
        DispatchQueue.main.async {
            self.tableview .reloadData()
            self.hud.dismiss()
        }
    }
    
    func showHud() -> Void
    {
        DispatchQueue.main.async {
            if !self.hud.isVisible
            {
                self.hud.textLabel.text = "Loading"
                self.hud.show(in: self.view)
            }
        }
    }
    
    
    
}

