-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Dic 14, 2021 alle 15:13
-- Versione del server: 10.4.22-MariaDB
-- Versione PHP: 8.0.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tutoring`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `bookings`
--

CREATE TABLE `bookings` (
  `id` int(11) NOT NULL,
  `teacherId` int(8) NOT NULL,
  `courseId` int(8) NOT NULL,
  `userId` varchar(16) NOT NULL,
  `day` enum('0','1','2','3','4') NOT NULL,
  `hour` enum('a','b','c','d') NOT NULL,
  `status` enum('booked','done','canceled') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `courses`
--

CREATE TABLE `courses` (
  `id` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` text DEFAULT NULL,
  `available` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `courses`
--

INSERT INTO `courses` (`id`, `name`, `description`, `available`) VALUES
(36, 'Italiano', 'Ripetizioni di italiano', 1),
(37, 'Storia', 'Ripetizioni di storia', 1),
(38, 'Matematica', 'Ripetizioni di matematica', 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `teachers`
--

CREATE TABLE `teachers` (
  `id` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `surname` varchar(32) NOT NULL,
  `available` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `teachers`
--

INSERT INTO `teachers` (`id`, `name`, `surname`, `available`) VALUES
(20, 'Mario', 'Rossi', 1),
(21, 'Luigi', 'Verdi', 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `teaches`
--

CREATE TABLE `teaches` (
  `teacherId` int(11) NOT NULL,
  `courseId` int(11) NOT NULL,
  `available` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `teaches`
--

INSERT INTO `teaches` (`teacherId`, `courseId`, `available`) VALUES
(20, 36, 1),
(20, 37, 1),
(21, 38, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `users`
--

CREATE TABLE `users` (
  `username` varchar(16) NOT NULL,
  `name` varchar(32) NOT NULL,
  `surname` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `admin` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `users`
--

INSERT INTO `users` (`username`, `name`, `surname`, `password`, `admin`) VALUES
('ab', 'Andrea', 'Birritteri', 'ab', 0),
('Rondinella2000', 'Raoul', 'Rondinella', '12345678', 0),
('root', 'root', 'root', 'root', 1);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `booking_ibfk_1` (`courseId`,`teacherId`),
  ADD KEY `booking_ibfk_2` (`userId`);

--
-- Indici per le tabelle `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indici per le tabelle `teachers`
--
ALTER TABLE `teachers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`,`surname`);

--
-- Indici per le tabelle `teaches`
--
ALTER TABLE `teaches`
  ADD PRIMARY KEY (`teacherId`,`courseId`),
  ADD KEY `courseId` (`courseId`);

--
-- Indici per le tabelle `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=812;

--
-- AUTO_INCREMENT per la tabella `courses`
--
ALTER TABLE `courses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT per la tabella `teachers`
--
ALTER TABLE `teachers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`courseId`,`teacherId`) REFERENCES `teaches` (`courseId`, `teacherId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `teaches`
--
ALTER TABLE `teaches`
  ADD CONSTRAINT `teaches_ibfk_1` FOREIGN KEY (`courseId`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `teaches_ibfk_2` FOREIGN KEY (`teacherId`) REFERENCES `teachers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
