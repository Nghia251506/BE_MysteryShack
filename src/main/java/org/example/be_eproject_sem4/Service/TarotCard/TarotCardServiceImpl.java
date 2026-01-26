package org.example.be_eproject_sem4.Service.TarotCard;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.Tarot.DrawTarotResponse;
import org.example.be_eproject_sem4.Dto.Tarot.DrawnCard;
import org.example.be_eproject_sem4.Dto.Tarot.InterpretSelectedRequest;
import org.example.be_eproject_sem4.Dto.TarotCard.CreateTarotCardDto;
import org.example.be_eproject_sem4.Dto.TarotCard.TarotCardResponseDto;
import org.example.be_eproject_sem4.Dto.TarotCard.UpdateTarotCardDto;
import org.example.be_eproject_sem4.Entity.Arcana;
import org.example.be_eproject_sem4.Entity.TarotCard;
import org.example.be_eproject_sem4.Mapper.TarotCardMapper;
import org.example.be_eproject_sem4.Repository.TarotCardRepository;
import org.example.be_eproject_sem4.Service.AI.AiInterpretationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TarotCardServiceImpl implements TarotCardService {

    private final TarotCardRepository tarotCardRepo;
    private final AiInterpretationService aiService;
    @Override
    @Transactional(readOnly = true)
    public Page<TarotCardResponseDto> getAllCards(Pageable pageable) {
        return tarotCardRepo.findAll(pageable)
                .map(TarotCardMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarotCardResponseDto> getAllActiveCards() {
        return tarotCardRepo.findByIsActiveTrue().stream()
                .map(TarotCardMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TarotCardResponseDto getCardById(Long id) {
        TarotCard card = tarotCardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lá bài với ID: " + id));
        return TarotCardMapper.toResponseDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public TarotCardResponseDto getCardByCardNumber(Integer cardNumber) {
        TarotCard card = tarotCardRepo.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lá bài với số thứ tự: " + cardNumber));
        return TarotCardMapper.toResponseDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public TarotCardResponseDto getCardByNameEn(String nameEn) {
        TarotCard card = tarotCardRepo.findByNameEnIgnoreCase(nameEn)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lá bài với tên: " + nameEn));
        return TarotCardMapper.toResponseDto(card);
    }

    @Override
    public TarotCardResponseDto createCard(CreateTarotCardDto dto) {
        // Kiểm tra trùng cardNumber hoặc nameEn
        if (tarotCardRepo.findByCardNumber(dto.getCardNumber()).isPresent()) {
            throw new RuntimeException("Số thứ tự lá bài đã tồn tại: " + dto.getCardNumber());
        }
        if (tarotCardRepo.findByNameEnIgnoreCase(dto.getNameEn()).isPresent()) {
            throw new RuntimeException("Tên lá bài tiếng Anh đã tồn tại: " + dto.getNameEn());
        }

        TarotCard card = TarotCardMapper.toEntity(dto);
        TarotCard saved = tarotCardRepo.save(card);
        return TarotCardMapper.toResponseDto(saved);
    }

    @Override
    public TarotCardResponseDto updateCard(Long id, UpdateTarotCardDto dto) {
        TarotCard card = tarotCardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lá bài với ID: " + id));

        // Kiểm tra trùng nếu thay đổi cardNumber hoặc nameEn
        if (dto.getCardNumber() != null && !dto.getCardNumber().equals(card.getCardNumber())) {
            if (tarotCardRepo.findByCardNumber(dto.getCardNumber()).isPresent()) {
                throw new RuntimeException("Số thứ tự đã tồn tại: " + dto.getCardNumber());
            }
        }
        if (dto.getNameEn() != null && !dto.getNameEn().equalsIgnoreCase(card.getNameEn())) {
            if (tarotCardRepo.findByNameEnIgnoreCase(dto.getNameEn()).isPresent()) {
                throw new RuntimeException("Tên lá bài tiếng Anh đã tồn tại: " + dto.getNameEn());
            }
        }

        TarotCardMapper.updateEntity(card, dto);
        TarotCard updated = tarotCardRepo.save(card);
        return TarotCardMapper.toResponseDto(updated);
    }

    @Override
    public void softDeleteCard(Long id) {
        TarotCard card = tarotCardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lá bài với ID: " + id));
        card.setActive(false);
        tarotCardRepo.save(card);
    }

    @Override
    public void deleteCard(Long id) {
        if (!tarotCardRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy lá bài với ID: " + id);
        }
        tarotCardRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarotCardResponseDto> searchActiveCards(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveCards();
        }
        return tarotCardRepo.searchActiveCards(query.trim()).stream()
                .map(TarotCardMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllCards() {
        return tarotCardRepo.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveCards() {
        return tarotCardRepo.findByIsActiveTrue().size();
    }

    @Override
    public DrawTarotResponse drawThreeCardsByTopic(String topic, LocalDate birthday) {
        List<TarotCard> selectedDeck = filterDeckByTopic(topic.trim().toLowerCase());

        if (selectedDeck.isEmpty()) {
            throw new RuntimeException("Không có lá bài phù hợp với chủ đề: " + topic);
        }

        Collections.shuffle(selectedDeck);

        List<DrawnCard> drawnCards = selectedDeck.stream()
                .limit(3)
                .map(card -> {
                    boolean isReversed = Math.random() < 0.5;
                    String meaning = isReversed ? card.getReversedMeaning() : card.getUprightMeaning();
                    TarotCardResponseDto cardDto = TarotCardMapper.toResponseDto(card);
                    return DrawnCard.builder()
                            .card(cardDto)
                            .isReversed(isReversed)
                            .meaning(meaning)
                            .build();
                })
                .toList();

        String aiInterpretation = aiService.generateInterpretation(topic, drawnCards, birthday);

        return new DrawTarotResponse(drawnCards, aiInterpretation);
    }
    @Override
    public List<TarotCardResponseDto> shuffleAndGetDeck (String topic) {
        List<TarotCard> deck = filterDeckByTopic(topic);
        List<TarotCard> mutableDeck = new ArrayList<>(deck);
        Collections.shuffle(mutableDeck);
        return mutableDeck.stream().map(TarotCardMapper::toResponseDto).toList();
    }

    @Override
    public DrawTarotResponse interpretSelectedCards(String topic, LocalDate birthday, List<InterpretSelectedRequest.SelectedCard> selected) {
        if(selected.size() != 3){
            throw new RuntimeException("Phải chọn đúng 3 lá");
        }
        List<DrawnCard> drawnCards = selected.stream().map(sel -> {
            TarotCard card = tarotCardRepo.findById(sel.getCardId())
                    .orElseThrow(() -> new RuntimeException("Lá bài không tồn tại"));
            String meaning = sel.isReversed() ? card.getReversedMeaning() : card.getUprightMeaning();
            return DrawnCard.builder()
                    .card(TarotCardMapper.toResponseDto(card))
                    .isReversed(sel.isReversed())
                    .meaning(meaning)
                    .build();
        }).toList();
        String aiText = aiService.generateInterpretation(topic, drawnCards, birthday);

        return new DrawTarotResponse(drawnCards, aiText);
    }

    private List<TarotCard> filterDeckByTopic(String topic) {
        List<TarotCard> activeCards = tarotCardRepo.findByIsActiveTrue();

        return switch (topic) {
            // Câu hỏi lớn, sâu sắc → chỉ Major Arcana
            case "tổng quát", "cuộc đời", "sứ mệnh", "định mệnh", "hành trình", "tâm linh" -> activeCards.stream()
                    .filter(card -> card.getArcana() == Arcana.MAJOR)
                    .toList();

            // Tình yêu → Cups + Major
            case "tình yêu", "tình cảm", "hẹn hò", "hôn nhân" -> activeCards.stream()
                    .filter(card -> card.getArcana() == Arcana.MAJOR ||
                            "CUPS".equalsIgnoreCase(card.getSuit()))
                    .toList();

            // Công việc, sự nghiệp → Wands + Pentacles + Major
            case "công việc", "sự nghiệp", "việc làm" -> activeCards.stream()
                    .filter(card -> card.getArcana() == Arcana.MAJOR ||
                            "WANDS".equalsIgnoreCase(card.getSuit()) ||
                            "PENTACLES".equalsIgnoreCase(card.getSuit()))
                    .toList();

            // Tài chính, tiền bạc → Pentacles + Major
            case "tài chính", "tiền bạc", "đầu tư" -> activeCards.stream()
                    .filter(card -> card.getArcana() == Arcana.MAJOR ||
                            "PENTACLES".equalsIgnoreCase(card.getSuit()))
                    .toList();

            // Sức khỏe, xung đột → Swords + Major
            case "sức khỏe", "xung đột", "tranh cãi" -> activeCards.stream()
                    .filter(card -> card.getArcana() == Arcana.MAJOR ||
                            "SWORDS".equalsIgnoreCase(card.getSuit()))
                    .toList();

            // Mặc định: toàn bộ bộ bài
            default -> activeCards;
        };
    }
}